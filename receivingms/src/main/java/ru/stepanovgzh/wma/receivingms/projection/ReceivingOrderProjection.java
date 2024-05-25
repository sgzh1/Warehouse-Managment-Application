package ru.stepanovgzh.wma.receivingms.projection;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.stepanovgzh.wma.receivingms.cqrs.event.CargoReceivedEvent;
import ru.stepanovgzh.wma.receivingms.cqrs.event.DetailAddedToReceivingOrderEvent;
import ru.stepanovgzh.wma.receivingms.cqrs.event.DetailRemovedFromReceivingOrderEvent;
import ru.stepanovgzh.wma.receivingms.cqrs.event.ReceivingOrderCreatedEvent;
import ru.stepanovgzh.wma.receivingms.cqrs.event.ReceivingOrderDeletedEvent;
import ru.stepanovgzh.wma.receivingms.cqrs.query.AllReceivingOrdersQuery;
import ru.stepanovgzh.wma.receivingms.data.entity.ReceivingOrderDetail;
import ru.stepanovgzh.wma.receivingms.data.mapper.ReceivingOrderMapper;
import ru.stepanovgzh.wma.receivingms.data.model.ReceivingOrder;
import ru.stepanovgzh.wma.receivingms.data.repository.ReceivingOrderDetailRepository;
import ru.stepanovgzh.wma.receivingms.data.repository.ReceivingOrderRepository;
import ru.stepanovgzh.wma.receivingms.data.view.ReceivingOrderView;

@Service
@RequiredArgsConstructor
public class ReceivingOrderProjection 
{
    private final ReceivingOrderMapper receivingOrderMapper;
    private final ReceivingOrderRepository receivingOrderRepository;
    private final ReceivingOrderDetailRepository receivingOrderDetailRepository;

    @EventHandler
    public void on(ReceivingOrderCreatedEvent receivingOrderCreatedEvent)
    {
        receivingOrderRepository.save(receivingOrderMapper.map(receivingOrderCreatedEvent));
    }

    @EventHandler
    public void on(DetailAddedToReceivingOrderEvent detailAddedToReceivingOrderEvent)
    {
        ReceivingOrder receivingOrder 
            = receivingOrderRepository
                .findById(detailAddedToReceivingOrderEvent.getReceivingOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Receiving order not found, id = " 
                    + detailAddedToReceivingOrderEvent.getReceivingOrderId()));
        receivingOrder.addDetail(detailAddedToReceivingOrderEvent.getDetail());
        receivingOrderRepository.save(receivingOrder);
    }

    @EventHandler
    public void on(DetailRemovedFromReceivingOrderEvent detailRemovedFromReceivingOrderEvent)
    {
        ReceivingOrder receivingOrder 
            = receivingOrderRepository
                .findById(detailRemovedFromReceivingOrderEvent.getReceivingOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Receiving order not found, id = " 
                    + detailRemovedFromReceivingOrderEvent.getReceivingOrderId()));
        ReceivingOrderDetail detail
                    = receivingOrderDetailRepository
                        .findById(detailRemovedFromReceivingOrderEvent.getDetailId())
                        .orElseThrow(() 
                            -> new EntityNotFoundException("Receiving order detail not found, id = " 
                                + detailRemovedFromReceivingOrderEvent.getDetailId()));
        receivingOrderDetailRepository.delete(detail);
        receivingOrderRepository.save(receivingOrder);
    }

    @EventHandler
    public void on(CargoReceivedEvent cargoReceivedEvent)
    {
        ReceivingOrder receivingOrder 
            = receivingOrderRepository.findById(cargoReceivedEvent.getReceivingOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Receiving order not found, id = " 
                    + cargoReceivedEvent.getReceivingOrderId()));
        ReceivingOrderDetail receivingOrderDetail 
            = receivingOrderDetailRepository.findById(cargoReceivedEvent.getDetailId())
                .orElseThrow(() 
                    -> new EntityNotFoundException("Receiving order detail not found, id = " 
                        + cargoReceivedEvent.getDetailId()));
        if (receivingOrder.getId().equals(receivingOrderDetail.getReceivingOrderId()))
        {
        }
    }

    @EventHandler
    public void on(ReceivingOrderDeletedEvent receivingOrderDeletedEvent)
    {
        ReceivingOrder receivingOrder 
            = receivingOrderRepository.findById(receivingOrderDeletedEvent.getId())
                .orElseThrow(() -> new EntityNotFoundException("Receiving order not found, id = " 
                    + receivingOrderDeletedEvent.getId()));
        receivingOrderDetailRepository.deleteByReceivingOrderId(receivingOrder.getId());
        receivingOrderRepository.delete(receivingOrder);
    }

    @QueryHandler
    public List<ReceivingOrderView> handleReceivingOrdersList(AllReceivingOrdersQuery allReceivingOrdersQuery)
    {
        return receivingOrderRepository.findAll().stream()
            .map(receivingOrder -> receivingOrderMapper.map(receivingOrder))
            .collect(Collectors.toList());
    }
}

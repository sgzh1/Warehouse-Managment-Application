package ru.stepanovgzh.wma.storingms.projection;

import java.util.List;
import java.util.stream.Collectors;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.stepanovgzh.wma.storingms.cqrs.event.CargoCreatedEvent;
import ru.stepanovgzh.wma.storingms.cqrs.event.CargoMovedEvent;
import ru.stepanovgzh.wma.storingms.cqrs.event.CargoStatusChangedEvent;
import ru.stepanovgzh.wma.storingms.cqrs.event.CargoUpdatedEvent;
import ru.stepanovgzh.wma.storingms.cqrs.query.AllCargoQuery;
import ru.stepanovgzh.wma.storingms.data.EntityMapper;
import ru.stepanovgzh.wma.storingms.data.model.Cargo;
import ru.stepanovgzh.wma.storingms.data.repository.CargoRepository;
import ru.stepanovgzh.wma.storingms.data.view.CargoView;

@Service
@RequiredArgsConstructor
public class СargoProjection 
{
    private final EntityMapper entityMapper;
    private final CargoRepository cargoRepository;

    @EventHandler
    public void on(CargoCreatedEvent cargoCreatedEvent)
    {
        cargoRepository.save(entityMapper.map(cargoCreatedEvent));
    }

    @EventHandler
    public void on(CargoMovedEvent cargoMovedEvent)
    {
        Cargo cargo = cargoRepository.findById(cargoMovedEvent.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Cargo not found, id = " + cargoMovedEvent.getId()));
        cargo.setLocation(cargoMovedEvent.getLocation());
        cargoRepository.save(cargo);
    }

    @EventHandler
    public void on(CargoStatusChangedEvent cargoStatusChangedEvent)
    {
        Cargo cargo = cargoRepository.findById(cargoStatusChangedEvent.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Cargo not found, id = " + cargoStatusChangedEvent.getId()));
        cargo.setStatus(cargoStatusChangedEvent.getStatus());
        cargoRepository.save(cargo);
    }

    @EventHandler
    public void on(CargoUpdatedEvent cargoUpdatedEvent)
    {
        Cargo cargoFromDb = cargoRepository.findById(cargoUpdatedEvent.getId())
            .orElseThrow(() -> new EntityNotFoundException(
                    "Cargo not found, id = " + cargoUpdatedEvent.getId()));
        Cargo cargoFromEvent = entityMapper.map(cargoUpdatedEvent);
        cargoRepository.save(entityMapper.merge(cargoFromEvent, cargoFromDb));
    }

    @QueryHandler
    public List<CargoView> handleCargoList(AllCargoQuery allCargoQuery)
    {
        return cargoRepository.findAll().stream()
            .map(cargo -> new CargoView(
                cargo.getId(),
                cargo.getSku().getBarcode(),
                cargo.getSku().getName(),
                cargo.getSku().getDescription(),
                cargo.getPack().getType(),
                cargo.getPack().getDescription(),
                cargo.getQty(),
                cargo.getLocation().getZone(),
                cargo.getLocation().getCell(),
                cargo.getStatus()))
            .collect(Collectors.toList());
    }
}

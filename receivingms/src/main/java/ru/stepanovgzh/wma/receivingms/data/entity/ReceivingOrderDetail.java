package ru.stepanovgzh.wma.receivingms.data.entity;

import java.util.UUID;

import jakarta.persistence.Id;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.stepanovgzh.wma.receivingms.data.value.Pack;
import ru.stepanovgzh.wma.receivingms.data.value.Sku;
import ru.stepanovgzh.wma.receivingms.data.value.SkuReceivingStatus;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ReceivingOrderDetail 
{
    @Id
    UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiving_order_id")
    UUID receivingOrderId;

    @Embedded
    Sku sku;

    int qty;

    @Embedded
    Pack pack;

    UUID receivedCargoId;

    SkuReceivingStatus skuReceivingStatus;

    public ReceivingOrderDetail(UUID id, UUID receivingOrderId, Sku sku, int qty, Pack pack)
    {
        this.id = id;
        this.receivingOrderId = receivingOrderId;
        this.sku = sku;
        this.qty = qty;
        this.pack = pack;
    }
}
package ru.stepanovgzh.wma.receivingms.cqrs.event;

import java.util.Date;
import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;
import ru.stepanovgzh.wma.receivingms.data.entity.Supplier;
import ru.stepanovgzh.wma.receivingms.data.entity.Transporter;

@Value
public class CreateReceivingOrderEvent 
{
    @TargetAggregateIdentifier
    UUID id;
    Supplier supplier;
    Transporter transporter;
    Date date;
}

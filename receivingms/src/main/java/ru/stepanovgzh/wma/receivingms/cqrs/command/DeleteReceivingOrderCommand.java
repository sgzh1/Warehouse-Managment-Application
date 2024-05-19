package ru.stepanovgzh.wma.receivingms.cqrs.command;

import java.util.UUID;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import lombok.Value;

@Value
public class DeleteReceivingOrderCommand 
{
    @TargetAggregateIdentifier
    UUID id;
}

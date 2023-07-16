package com.example.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter @Setter
@EqualsAndHashCode
@ToString
public class SubscriptionId implements Serializable {
    private User publisher;
    private User subscriber;
}

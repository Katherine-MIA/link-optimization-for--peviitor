package org.example;

import org.example.service_invalid.ActionInitiator;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        ActionInitiator actionInitiator = new ActionInitiator();
        List<String> urlsForDeletion = actionInitiator.threadInitiator();

    }
}
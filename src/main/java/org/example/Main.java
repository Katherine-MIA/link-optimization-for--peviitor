package org.example;

import org.example.service.Delegator;
import org.example.service_invalid.ActionInitiator;

import java.util.List;

public class Main {
    public void invalidServiceStart(){
        ActionInitiator actionInitiator = new ActionInitiator();
        List<String> urlsForDeletion = actionInitiator.threadInitiator();
        System.out.println("SIZE: " + urlsForDeletion.size());
        System.out.println(urlsForDeletion);
    }


    public static void main(String[] args) {
        Delegator delegator = new Delegator();
        System.out.println(delegator.callOnePage());
        delegator.startSendingRequests();
    }
}
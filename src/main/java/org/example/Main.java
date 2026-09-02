package org.example;

import org.example.dtos.JobStatus;
import org.example.dtos.UpdateDTO;
import org.example.model.JobLink;
import org.example.service.Delegator;
import org.example.service_invalid.ActionInitiator;
import org.example.service_v2.APICaller;
import org.example.service_v2.TaskDelegator;

import java.util.List;

public class Main {
    public void invalidServiceStart(){
        ActionInitiator actionInitiator = new ActionInitiator();
        List<String> urlsForDeletion = actionInitiator.threadInitiator();
        System.out.println("SIZE: " + urlsForDeletion.size());
        System.out.println(urlsForDeletion);
    }

    public static void serviceStart(){
        Delegator delegator = new Delegator();
        System.out.println(delegator.callOnePage());
        delegator.startSendingRequests();
    }

    public static void serviceV2Start() {
        TaskDelegator delegator = new TaskDelegator();
        delegator.startSendingRequests();
    }

    public static void main(String[] args) {
        //serviceStart();
        serviceV2Start();
    }
}
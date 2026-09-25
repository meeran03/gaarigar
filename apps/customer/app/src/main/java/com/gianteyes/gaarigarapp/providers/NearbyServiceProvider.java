package com.gianteyes.gaarigarapp.providers;

import com.gianteyes.gaarigarapp.models.ServiceProvider;

import java.util.List;

public class NearbyServiceProvider {
    private static NearbyServiceProvider instance;
    Long requestId;
    List<ServiceProvider> servicers;

    private NearbyServiceProvider() {
    }

    public static NearbyServiceProvider getInstance() {
        if (NearbyServiceProvider.instance == null) {
            NearbyServiceProvider.instance = new NearbyServiceProvider();
        }
        return NearbyServiceProvider.instance;
    }

    public Long getRequestId() {
        return this.requestId;
    }

    public void setRequestId(final Long requestId) {
        this.requestId = requestId;
    }

    public List<ServiceProvider> getNearbyServicers() {
        return this.servicers;
    }

    public void setNearbyServices(final List<ServiceProvider> servicers) {
        this.servicers = servicers;
    }

    public ServiceProvider findByPhone(final String phone) {
        for (final ServiceProvider mechanic : this.servicers) {
            if (mechanic.getPhone().equals(phone)) {
                return mechanic;
            }
        }
        return null;
    }

}

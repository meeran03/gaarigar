package com.gianteyes.gaarigar.Order;

import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelDeliveryOrderModel;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderModel;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrder;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.Order.dto.OrderUser;
import com.gianteyes.gaarigar.Order.dto.StandardService;
import com.gianteyes.gaarigar.utils.FileUpload;
import com.gianteyes.gaarigar.utils.GeoUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderMapper {
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    FileUpload fileUpload;

    @Autowired
    GeoUtils geoUtils;

    public OrderResponseDto mapMechanicOrderToOrderResponse(MechanicOrderModel order) {
        OrderResponseDto res = modelMapper.map(order, OrderResponseDto.class);
        res.setCustomer(
                modelMapper.map(order.getCustomer(), OrderUser.class)
        );
        res.getCustomer().setImage(
                fileUpload.generateUrl(order.getCustomer().getImage())
        );
        res.getCustomer().setLocation(
                order.getCustomerLocation()
        );
        res.getCustomer().setName(
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()
        );
        res.setMechanic(
                modelMapper.map(order.getMechanic(), OrderUser.class)
        );
        res.getMechanic().setName(
                order.getMechanic().getFirstName() + " " + order.getMechanic().getLastName()
        );
        res.getMechanic().setImage(
                fileUpload.generateUrl(order.getMechanic().getImage())
        );
        res.getMechanic().setLocation(
                geoUtils.convertPointToLocation(order.getMechanic().getLocation())
        );
        return res;
    }

    public OrderResponseDto mapStandardServiceOrderToOrderResponse(StandardServiceOrder order) {
        OrderResponseDto res = modelMapper.map(order, OrderResponseDto.class);
        res.setCustomer(
                modelMapper.map(order.getCustomer(), OrderUser.class)
        );
        res.getCustomer().setName(
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()
        );
        res.getCustomer().setImage(
                fileUpload.generateUrl(order.getCustomer().getImage())
        );
        res.getCustomer().setLocation(
                order.getCustomerLocation()
        );
        res.setMechanic(
                modelMapper.map(order.getMechanicStandardService().getMechanic(), OrderUser.class)
        );
        res.getMechanic().setName(
                order.getMechanicStandardService().getMechanic().getFirstName() + " " + order.getMechanicStandardService().getMechanic().getLastName()
        );
        res.getMechanic().setImage(
                fileUpload.generateUrl(order.getMechanicStandardService().getMechanic().getImage())
        );
        res.setStandardService(
                modelMapper.map(order.getMechanicStandardService().getStandardService(), StandardService.class)
        );
        res.getStandardService().setImage(
                fileUpload.generateUrl(order.getMechanicStandardService().getStandardService().getImage())
        );
        res.getStandardService().setPrice(
                order.getMechanicStandardService().getPrice()
        );
        res.getMechanic().setLocation(
                geoUtils.convertPointToLocation(order.getMechanicStandardService().getMechanic().getLocation())
        );
        return res;
    }

    public List<OrderResponseDto> mapToMechanicOrderResponseDto(List<MechanicOrderModel> mechanicOrderModels) {
        return mechanicOrderModels.stream().map(this::mapMechanicOrderToOrderResponse).toList();
    }

    public OrderResponseDto mapFuelDeliveryOrderToOrderResponse(FuelDeliveryOrderModel order) {
        OrderResponseDto res = modelMapper.map(order, OrderResponseDto.class);
        res.setCustomer(
                modelMapper.map(order.getCustomer(), OrderUser.class)
        );
        res.getCustomer().setName(
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName()
        );
        res.getCustomer().setImage(
                fileUpload.generateUrl(order.getCustomer().getImage())
        );
        res.setPetrolPump(
                modelMapper.map(order.getPetrolPump(), OrderUser.class)
        );
        res.getPetrolPump().setName(
                order.getPetrolPump().getFirstName() + " " + order.getPetrolPump().getLastName()
        );
        res.getCustomer().setLocation(
                order.getCustomerLocation()
        );
        res.getPetrolPump().setImage(
                fileUpload.generateUrl(order.getPetrolPump().getImage())
        );
        res.getPetrolPump().setLocation(
                geoUtils.convertPointToLocation(order.getPetrolPump().getLocation())
        );
        res.setLitres(order.getNoOfLitres());
        return res;
    }


    public List<OrderResponseDto> mapToStandardServiceOrderResponseDto(List<StandardServiceOrder> standardServiceOrders) {
        return standardServiceOrders.stream().map(this::mapStandardServiceOrderToOrderResponse).toList();
    }

    public List<OrderResponseDto> mapToFuelDeliveryOrderResponseDto(List<FuelDeliveryOrderModel> fuelDeliveryOrderModels) {
        return fuelDeliveryOrderModels.stream().map(this::mapFuelDeliveryOrderToOrderResponse).toList();
    }
}

package com.gianteyes.gaarigar.bootstrap;

import com.gianteyes.gaarigar.common.admin.AdminModel;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.mechanic.*;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.user.*;
import com.gianteyes.gaarigar.category.CategoryModel;
import com.gianteyes.gaarigar.standardservice.*;
import com.gianteyes.gaarigar.Order.*;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderModel;
import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelDeliveryOrderModel;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrder;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.payment.PaymentMethod;
import jakarta.persistence.EntityManager;
import org.locationtech.jts.geom.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class SampleData implements ApplicationRunner {
    private final EntityManager em;
    private final UserRepository users;
    private final PasswordEncoder passwords;
    @Value("${gaarigar.sample-data:false}") private boolean sample;
    @Value("${gaarigar.admin.phone}") private String adminPhone;
    @Value("${gaarigar.admin.password}") private String adminPassword;
    @Value("${gaarigar.sample.password:}") private String samplePassword;
    public SampleData(EntityManager em, UserRepository users, PasswordEncoder passwords) {this.em=em;this.users=users;this.passwords=passwords;}
    @Override @Transactional public void run(ApplicationArguments args) {
        if (adminPassword.length()<16) throw new IllegalStateException("ADMIN_PASSWORD must contain at least 16 characters");
        if (!users.findByPhone(adminPhone).isPresent()) {
            AdminModel admin=new AdminModel(); fill(admin,adminPhone,"GaariGar","Administrator",UserType.ADMIN,adminPassword,0); em.persist(admin);
        }
        if (!sample || users.findByPhone("+199955501001").isPresent()) return;
        if(samplePassword.length()<12) throw new IllegalStateException("SAMPLE_PASSWORD must contain at least 12 characters");
        AdminModel viewer=new AdminModel(); fill(viewer,"+199955501099","Sample","Viewer",UserType.DEMO_ADMIN,samplePassword,0); em.persist(viewer);
        var customers=new ArrayList<CustomerModel>();
        String[] names={"Amina","Hassan","Sara","Omar","Noor","Bilal","Zoya","Daniyal"};
        for(int i=0;i<names.length;i++) {
            CustomerModel c=new CustomerModel();fill(c,String.format("+199955501%03d",i+1),names[i],"Sample",UserType.CUSTOMER,samplePassword,i);
            c.setEmail("customer"+(i+1)+"@example.invalid");em.persist(c);customers.add(c);
        }
        var mechanics=new ArrayList<MechanicModel>();
        String[] shops={"Margalla Auto","Blue Area Motors","Capital Tyres","Metro Electric","Twin City Garage","Park Road Auto"};
        for(int i=0;i<shops.length;i++) {
            MechanicModel m=new MechanicModel();fill(m,String.format("+199955502%03d",i+1),shops[i],"Sample",UserType.MECHANIC,samplePassword,i+1);
            m.setType(MechanicType.values()[i]);m.setIsAvailable(true);m.setRating(4.3+i*.1);em.persist(m);mechanics.add(m);
        }
        var pumps=new ArrayList<PetrolPumpModel>();
        for(int i=0;i<3;i++) {
            PetrolPumpModel p=new PetrolPumpModel();fill(p,String.format("+199955503%03d",i+1),"Fuel Station "+(i+1),"Sample",UserType.PETROL_PUMP,samplePassword,i+2);
            p.setAddress("Sample location, Islamabad");p.setIsAvailable(true);em.persist(p);pumps.add(p);
        }
        String[] categories={"Routine maintenance","Tyres & wheels","Electrical","Roadside assistance"};
        var cats=new ArrayList<CategoryModel>();
        for(String name:categories){var c=new CategoryModel();c.setName(name);em.persist(c);cats.add(c);}
        String[] titles={"Oil and filter change","Wheel alignment","Battery inspection","Roadside diagnosis","Brake inspection","Puncture repair"};
        var offerings=new ArrayList<MechanicStandardServiceModel>();
        for(int i=0;i<titles.length;i++) {
            var s=new StandardServiceModel();s.setName(titles[i]);s.setDescription("Sample service for exploring GaariGar. No real booking or payment.");s.setCategory(cats.get(i%cats.size()));em.persist(s);
            var o=new MechanicStandardServiceModel();o.setMechanic(mechanics.get(i));o.setStandardService(s);o.setPrice(1500d+i*500);o.setIsActive(true);em.persist(o);offerings.add(o);
        }
        for(int i=0;i<36;i++) {
            OrderModel order;
            if(i%3==0){var o=new MechanicOrderModel();o.setMechanic(mechanics.get(i%mechanics.size()));order=o;order.setOrderType(OrderType.MECHANIC);}
            else if(i%3==1){var o=new FuelDeliveryOrderModel();o.setPetrolPump(pumps.get(i%pumps.size()));o.setDistance(3);o.setNoOfLitres(5);o.setLitrePrice(280);order=o;order.setOrderType(OrderType.FUEL_DELIVERY);}
            else{var o=new StandardServiceOrder();o.setMechanicStandardService(offerings.get(i%offerings.size()));order=o;order.setOrderType(OrderType.STANDARD_SERVICE);}
            LocalDateTime when=LocalDateTime.now().minusDays(i%14).minusHours(2);
            order.setCustomer(customers.get(i%customers.size()));order.setCustomerLocation(Location.builder().latitude(33.6844).longitude(73.0479).build());
            order.setRequestedAt(when);order.setAcceptedAt(when.plusMinutes(8));order.setPrice(1500d+(i%6)*500);order.setPaymentMethod(PaymentMethod.CASH);order.setNotes("Fictional sample booking");
            OrderStatus status=i%7==0?OrderStatus.CANCELLED:i%5==0?OrderStatus.IN_PROGRESS:OrderStatus.COMPLETED;order.setStatus(status);
            if(status==OrderStatus.COMPLETED)order.setCompletedAt(when.plusHours(1));if(status==OrderStatus.CANCELLED)order.setCancelledAt(when.plusMinutes(12));
            em.persist(order);
        }
    }
    private void fill(UserModel u,String phone,String first,String last,UserType type,String password,int offset) {
        u.setPhone(phone);u.setFirstName(first);u.setLastName(last);u.setUserType(type);u.setPassword(passwords.encode(password));u.setIsActive(true);u.setIsVerified(true);u.setEmailNotifications(false);u.setCreatedAt(LocalDateTime.now().minusDays(offset%14));
        u.setLocation(new GeometryFactory(new PrecisionModel(),4326).createPoint(new Coordinate(73.0479+offset*.003,33.6844+offset*.002)));
    }
}

package com.gianteyes.gaarigar.security;
import com.gianteyes.gaarigar.user.*;
import com.gianteyes.gaarigar.Order.*;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderModel;
import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelDeliveryOrderModel;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.StandardServiceOrder;
import com.gianteyes.gaarigar.Order.InitiatedRequest.InitiateRequestRepository;
import com.gianteyes.gaarigar.standardservice.dao.MechanicStandardServiceRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Objects;
@Component("access")
public class AccessPolicy {
 private final UserRepository users; private final OrderRepository orders; private final InitiateRequestRepository requests;private final MechanicStandardServiceRepository offerings;
 public AccessPolicy(UserRepository users,OrderRepository orders,InitiateRequestRepository requests,MechanicStandardServiceRepository offerings){this.users=users;this.orders=orders;this.requests=requests;this.offerings=offerings;}
 private UserModel actor(){var a=SecurityContextHolder.getContext().getAuthentication();return a==null?null:users.findByPhone(a.getName()).orElse(null);}
 public boolean sameUser(Long id){var u=actor();return u!=null&&(u.getUserType()==UserType.ADMIN||Objects.equals(u.getId(),id));}
 public boolean role(String name){var u=actor();return u!=null&&(u.getUserType()==UserType.ADMIN||u.getUserType().name().equals(name));}
 public boolean order(Long id,boolean providerOnly){return orderForUser(id,providerOnly,actor());}
 public boolean chat(Long id,String phone){return orderForUser(id,false,users.findByPhone(phone).orElse(null));}
 private boolean orderForUser(Long id,boolean providerOnly,UserModel u){var o=orders.findById(id).orElse(null);if(u==null||o==null)return false;if(u.getUserType()==UserType.ADMIN)return true;
  if(!providerOnly&&Objects.equals(o.getCustomer().getId(),u.getId()))return true;
  if(o instanceof MechanicOrderModel m)return Objects.equals(m.getMechanic().getId(),u.getId());
  if(o instanceof FuelDeliveryOrderModel f)return Objects.equals(f.getPetrolPump().getId(),u.getId());
  if(o instanceof StandardServiceOrder s)return Objects.equals(s.getMechanicStandardService().getMechanic().getId(),u.getId());return false;
 }
 public boolean requestOwner(Long id){var r=requests.findById(id).orElse(null);return r!=null&&sameUser(r.getCustomer().getId());}
 public boolean offering(Long id){var o=offerings.findById(id).orElse(null);return o!=null&&sameUser(o.getMechanic().getId());}
 public boolean nearbyRequest(Long id,String role){var u=actor();var r=requests.findById(id).orElse(null);if(u==null||r==null)return false;if(u.getUserType()==UserType.ADMIN)return true;if(!u.getUserType().name().equals(role)||u.getLocation()==null||r.getLocation()==null)return false;
  double a=Math.toRadians(u.getLocation().getY()), b=Math.toRadians(r.getLocation().getLatitude());
  double dlat=b-a,dlon=Math.toRadians(r.getLocation().getLongitude()-u.getLocation().getX());
  double h=Math.pow(Math.sin(dlat/2),2)+Math.cos(a)*Math.cos(b)*Math.pow(Math.sin(dlon/2),2);return 6371*2*Math.asin(Math.min(1,Math.sqrt(h)))<=5;
 }
}

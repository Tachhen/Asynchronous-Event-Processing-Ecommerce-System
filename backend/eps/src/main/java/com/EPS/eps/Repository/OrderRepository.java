package com.EPS.eps.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.EPS.eps.Entity.Order;

public interface OrderRepository extends JpaRepository<Order,Long>{
    
}

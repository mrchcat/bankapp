package com.github.mrchcat.notifications.Repository;

import com.github.mrchcat.notifications.domain.BankNotification;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<BankNotification, Long> {

    @Query("""
            SELECT *
            FROM notifications
            WHERE is_processed=false
            """)
    List<BankNotification> findAllNotProcessed();

    @Query("""
            UPDATE notifications
            SET is_processed=true
            WHERE id=:id
            RETURNING id
            """)
    long setProcessed(long id);


}

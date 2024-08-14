package org.zerock.chain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_emp_rooms")
public class ChatEmpRooms {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatNo;

    @ManyToOne
    @JoinColumn(name = "chat_room_no")
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "emp_no")
    private Employee employee;
}
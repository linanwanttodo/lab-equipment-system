package com.barezen.config;

import com.barezen.entity.Equipment;
import com.barezen.entity.EquipmentStatus;
import com.barezen.entity.Lab;
import com.barezen.entity.Role;
import com.barezen.entity.User;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LabRepository;
import com.barezen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时初始化演示数据：管理员 + 学生账号，以及若干实验室与设备（幂等，仅首次写入）。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, LabRepository labRepository,
                           EquipmentRepository equipmentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setName("系统管理员");
            admin.setContact("admin@lab.edu");
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            log.info("已创建管理员账号 admin / admin123");
        }
        if (!userRepository.existsByUsername("student")) {
            User student = new User();
            student.setUsername("student");
            student.setPassword(passwordEncoder.encode("123456"));
            student.setName("演示学生");
            student.setContact("student@lab.edu");
            student.setRole(Role.STUDENT);
            userRepository.save(student);
            log.info("已创建学生账号 student / 123456");
        }
        if (labRepository.count() == 0) {
            seedData();
        }
    }

    private void seedData() {
        Lab lab1 = new Lab();
        lab1.setName("物理实验室");
        lab1.setLocation("A 栋 301");
        labRepository.save(lab1);

        Lab lab2 = new Lab();
        lab2.setName("化学实验室");
        lab2.setLocation("A 栋 402");
        labRepository.save(lab2);

        saveEquipment("PHY-001", "示波器", "仪器", lab1, 10, 0.0, "用于信号观测");
        saveEquipment("PHY-002", "激光笔", "仪器", lab1, 20, 20.0, "教学演示用");
        saveEquipment("PHY-003", "高倍显微镜", "仪器", lab1, 5, 50.0, "光学显微镜");
        saveEquipment("CHE-001", "酒精灯", "耗材", lab2, 50, 5.0, "实验室常用");
        saveEquipment("CHE-002", "量筒(100ml)", "耗材", lab2, 30, 0.0, "玻璃量筒");
        log.info("已初始化演示实验室与设备数据");
    }

    private void saveEquipment(String code, String name, String category, Lab lab,
                               int total, double deposit, String desc) {
        Equipment e = new Equipment();
        e.setCode(code);
        e.setName(name);
        e.setCategory(category);
        e.setLab(lab);
        e.setTotalQty(total);
        e.setAvailableQty(total);
        e.setDeposit(deposit);
        e.setStatus(EquipmentStatus.AVAILABLE);
        e.setDescription(desc);
        equipmentRepository.save(e);
    }
}

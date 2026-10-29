package com.barezen.config;

import com.barezen.entity.Equipment;
import com.barezen.entity.EquipmentStatus;
import com.barezen.entity.Lab;
import com.barezen.entity.Loan;
import com.barezen.entity.LoanStatus;
import com.barezen.entity.Role;
import com.barezen.entity.User;
import com.barezen.repository.EquipmentRepository;
import com.barezen.repository.LabRepository;
import com.barezen.repository.LoanRepository;
import com.barezen.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 启动时初始化演示数据（幂等，仅首次写入；与 sql/init.sql 保持一致）：
 * 1 名管理员 + 5 名学生、4 个实验室、17 台设备、5 条借用记录样例
 * （借用中 / 已归还 / 即将逾期各若干，其中一条已过应还时间，
 * 启动后由逾期扫描任务自动标记为 OVERDUE 并触发提醒）。
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final LabRepository labRepository;
    private final EquipmentRepository equipmentRepository;
    private final LoanRepository loanRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, LabRepository labRepository,
                           EquipmentRepository equipmentRepository, LoanRepository loanRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.labRepository = labRepository;
        this.equipmentRepository = equipmentRepository;
        this.loanRepository = loanRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUsers();
        if (labRepository.count() == 0) {
            seedLabsAndEquipment();
        }
        if (loanRepository.count() == 0) {
            seedLoans();
        }
    }

    private void seedUsers() {
        createUser("admin", "admin123", "系统管理员", "admin@lab.edu", Role.ADMIN);
        createUser("student", "123456", "演示学生", "student@lab.edu", Role.STUDENT);
        createUser("liwei", "123456", "李伟", "liwei@lab.edu", Role.STUDENT);
        createUser("zhangmin", "123456", "张敏", "zhangmin@lab.edu", Role.STUDENT);
        createUser("chenjie", "123456", "陈杰", "chenjie@lab.edu", Role.STUDENT);
        createUser("wangfang", "123456", "王芳", "wangfang@lab.edu", Role.STUDENT);
    }

    private void createUser(String username, String rawPassword, String name, String contact, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setName(name);
        u.setContact(contact);
        u.setRole(role);
        userRepository.save(u);
        log.info("已创建演示账号 {} / {}", username, rawPassword);
    }

    private void seedLabsAndEquipment() {
        Lab physics = saveLab("物理实验室", "A 栋 301");
        Lab chemistry = saveLab("化学实验室", "A 栋 402");
        Lab biology = saveLab("生物实验室", "B 栋 205");
        Lab cs = saveLab("计算机实验室", "C 栋 501");

        // 物理实验室
        saveEquipment("PHY-001", "示波器", "仪器", physics, 10, 0.0, "用于信号观测");
        saveEquipment("PHY-002", "激光笔", "仪器", physics, 20, 20.0, "教学演示用");
        saveEquipment("PHY-003", "高倍显微镜", "仪器", physics, 5, 50.0, "光学显微镜");
        saveEquipment("PHY-004", "万用表", "仪器", physics, 15, 30.0, "数字万用表");
        saveEquipment("PHY-005", "信号发生器", "仪器", physics, 6, 60.0, "函数信号发生器");
        // 化学实验室
        saveEquipment("CHE-001", "酒精灯", "耗材", chemistry, 50, 5.0, "实验室常用");
        saveEquipment("CHE-002", "量筒(100ml)", "耗材", chemistry, 30, 0.0, "玻璃量筒");
        saveEquipment("CHE-003", "电子天平", "仪器", chemistry, 8, 40.0, "精度 0.01g");
        saveEquipment("CHE-004", "烧杯套装", "耗材", chemistry, 60, 0.0, "多种规格组合");
        saveEquipment("CHE-005", "pH计", "仪器", chemistry, 10, 45.0, "台式酸度计");
        // 生物实验室
        saveEquipment("BIO-001", "生物显微镜", "仪器", biology, 8, 80.0, "1600 倍光学放大");
        saveEquipment("BIO-002", "离心机", "仪器", biology, 4, 100.0, "台式高速离心机");
        saveEquipment("BIO-003", "移液器", "耗材", biology, 40, 0.0, "微量移液器套装");
        // 计算机实验室
        saveEquipment("CS-001", "笔记本电脑", "设备", cs, 15, 500.0, "编程实训用");
        saveEquipment("CS-002", "投影仪", "设备", cs, 6, 200.0, "会议教学投屏");
        saveEquipment("CS-003", "树莓派开发套件", "设备", cs, 10, 150.0, "含配件与电源");
        saveEquipment("CS-004", "3D打印机", "设备", cs, 2, 300.0, "FDM 桌面级");

        log.info("已初始化演示实验室与设备数据（4 实验室 / 17 台设备）");
    }

    private Lab saveLab(String name, String location) {
        Lab lab = new Lab();
        lab.setName(name);
        lab.setLocation(location);
        return labRepository.save(lab);
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

    /** 借用记录样例：与 sql/init.sql 的记录一致，依赖上面刚写入的实体关系。 */
    private void seedLoans() {
        User liwei = userRepository.findByUsername("liwei").orElse(null);
        User zhangmin = userRepository.findByUsername("zhangmin").orElse(null);
        User chenjie = userRepository.findByUsername("chenjie").orElse(null);
        User wangfang = userRepository.findByUsername("wangfang").orElse(null);
        User student = userRepository.findByUsername("student").orElse(null);

        Equipment oscilloscope = equipmentRepository.findByCode("PHY-001").orElse(null);
        Equipment laptop = equipmentRepository.findByCode("CS-001").orElse(null);
        Equipment microscope = equipmentRepository.findByCode("PHY-003").orElse(null);
        Equipment lamp = equipmentRepository.findByCode("CHE-001").orElse(null);
        Equipment printer = equipmentRepository.findByCode("CS-004").orElse(null);

        if (liwei == null || zhangmin == null || chenjie == null || wangfang == null || student == null
                || oscilloscope == null || laptop == null || microscope == null || lamp == null || printer == null) {
            log.warn("借用记录种子数据缺少前置用户/设备，跳过");
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // 李伟 借 示波器（借用中，5 天后到期）
        saveLoan(liwei, oscilloscope, "电子电路实验", now.minusDays(2), now.plusDays(5), null, LoanStatus.BORROWING);
        // 张敏 借 笔记本电脑（借用中，13 天后到期）
        saveLoan(zhangmin, laptop, "程序设计课程作业", now.minusDays(1), now.plusDays(13), null, LoanStatus.BORROWING);
        // 陈杰 借 高倍显微镜（已超期 3 天未还，启动后由定时任务标记逾期）
        saveLoan(chenjie, microscope, "光学实验观察", now.minusDays(10), now.minusDays(3), null, LoanStatus.BORROWING);
        // 王芳 借 酒精灯（按时归还样例）
        saveLoan(wangfang, lamp, "化学演示实验", now.minusDays(10), now.minusDays(3), now.minusDays(4), LoanStatus.RETURNED);
        // 演示学生 借 3D打印机（借用中，7 天后到期）
        saveLoan(student, printer, "毕业设计模型打印", now, now.plusDays(7), null, LoanStatus.BORROWING);

        log.info("已初始化演示借用记录（5 条）");
    }

    private void saveLoan(User user, Equipment equipment, String purpose,
                          LocalDateTime borrowTime, LocalDateTime dueTime,
                          LocalDateTime returnTime, LoanStatus status) {
        Loan loan = new Loan();
        loan.setUser(user);
        loan.setEquipment(equipment);
        loan.setPurpose(purpose);
        loan.setBorrowTime(borrowTime);
        loan.setDueTime(dueTime);
        loan.setReturnTime(returnTime);
        loan.setStatus(status);
        loan.setOverdueDays(0);
        loanRepository.save(loan);

        // 借出即占用库存，与真实借用流程保持一致
        if (status != LoanStatus.RETURNED) {
            equipment.setAvailableQty(Math.max(0, equipment.getAvailableQty() - 1));
            equipmentRepository.save(equipment);
        }
    }
}

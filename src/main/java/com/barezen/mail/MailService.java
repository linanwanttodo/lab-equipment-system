package com.barezen.mail;

import com.barezen.entity.Loan;
import com.barezen.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String from;

    public MailService(JavaMailSender mailSender,
                       @Value("${app.mail.enabled}") boolean enabled,
                       @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.from = from;
    }

    /** 发送逾期提醒邮件。未启用或无邮箱时仅记录日志；发送失败不影响主流程。 */
    public void sendOverdueNotice(User user, Loan loan) {
        String equipName = loan.getEquipment() != null ? loan.getEquipment().getName() : "";
        String equipCode = loan.getEquipment() != null ? loan.getEquipment().getCode() : "";
        if (!enabled) {
            log.info("邮件提醒未启用，跳过。用户={}, 设备={}", user.getUsername(), equipName);
            return;
        }
        String to = user.getContact();
        if (to == null || !to.contains("@")) {
            log.warn("用户 {} 未填写邮箱，跳过逾期邮件", user.getUsername());
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(from);
            msg.setTo(to);
            msg.setSubject("【实验室设备】逾期提醒：" + equipName);
            int days = loan.getOverdueDays() != null ? loan.getOverdueDays() : 0;
            msg.setText(String.format(
                    "您好 %s，您借用的设备【%s】(编号 %s) 已超过应还时间，逾期 %d 天，请尽快归还。",
                    user.getName(), equipName, equipCode, days));
            mailSender.send(msg);
            log.info("逾期邮件已发送: {} <- {}", to, equipName);
        } catch (Exception e) {
            log.error("发送逾期邮件失败: {}", e.getMessage());
        }
    }
}

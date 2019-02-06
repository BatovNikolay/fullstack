package ru.nbatov.service.impl;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;
import ru.nbatov.model.Notification;

import javax.mail.Session;
import java.util.Properties;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Session.class)
public class VanillaMailServiceTest {

    private VanillaMailService serviceMail;
    private Notification notification;
    private Properties prop;

    private Environment environment = mock(Environment.class);
    private Session session = mock(Session.class);

    @Before
    public void setUp() throws Exception {
        serviceMail = new VanillaMailService(environment);
        notification = Notification.builder()
                .to("test@test.ru")
                .subject("test")
                .body("test")
                .build();
    }

    private void initProps() {
        when(environment.getProperty("mail.smtp.host")).thenReturn("test.ru");
        when(environment.getProperty("mail.smtp.socketFactory.port")).thenReturn("1234");
        when(environment.getProperty("mail.smtp.socketFactory.class")).thenReturn("javax.net.ssl.SSLSocketFactory");
        when(environment.getProperty("mail.smtp.auth")).thenReturn("true");
        when(environment.getProperty("mail.smtp.port")).thenReturn("1234");
        when(environment.getProperty("mail.pop3.host")).thenReturn("test.ru");
        when(environment.getProperty("mail.pop3.port")).thenReturn("1234");
        when(environment.getProperty("mail.pop3.starttls.enable")).thenReturn("true");
    }

//    @Test
//    public void testSendMail() {
//        when(Session.getInstance(VanillaMailService.getPropPop3())).thenReturn(session);
//        initProps();
//        boolean result = serviceMail.sendMail(notification);
//        assertThat(false, is(result));
//    }
//
//    @Test
//    public void testGetMails() {
//        initProps();
//        List<Notification> mails = serviceMail.getMails();
//        assertThat(Collections.singletonList(Notification.builder()
//                .error(Error.builder()
//                        .errorMessage(ErrorMessage.INTERNAL_ERROR)
//                        .message("")
//                        .build())
//                .build()), is(mails));
//    }
//
//    @Test
//    public void testInitProps() {
//        initProps();
//        serviceMail.getMails();
//
//        Properties exp = VanillaMailService.getPropPop3();
//        prop = new Properties();
//        prop.put("mail.pop3.host", "test.ru");
//        prop.put("mail.pop3.port", "1234");
//        prop.put("mail.pop3.starttls.enable", "true");
//        assertThat(prop, is(exp));
//
//        exp = VanillaMailService.getPropSmtp();
//        prop.clear();
//        prop.put("mail.smtp.host", "test.ru");
//        prop.put("mail.smtp.socketFactory.port", "1234");
//        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
//        prop.put("mail.smtp.auth", "true");
//        prop.put("mail.smtp.port", "1234");
//        assertThat(prop, is(exp));
//    }
}

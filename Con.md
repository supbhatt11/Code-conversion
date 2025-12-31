import java.io.FileInputStream;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.search.SubjectTerm;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;

import org.json.JSONObject;

public class LegacyEmailToJira {

    public static void main(String[] args) {
        System.out.println("Starting Legacy Email to Jira Tool...");

        try {
            Properties config = loadConfig("config.properties");
            processEmails(config);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Process completed.");
    }

    private static Properties loadConfig(String filePath) throws Exception {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(filePath);
        props.load(fis);
        fis.close();
        return props;
    }

    private static void processEmails(Properties config) throws Exception {

        Properties mailProps = new Properties();
        mailProps.put("mail.store.protocol", "imaps");

        Session session = Session.getDefaultInstance(mailProps);
        Store store = session.getStore();

        store.connect(
                config.getProperty("mail.imap.host"),
                config.getProperty("mail.username"),
                config.getProperty("mail.password")
        );

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.search(new SubjectTerm("Request"));

        for (Message message : messages) {
            String subject = message.getSubject();
            String content = getEmailContent(message);

            System.out.println("Processing email: " + subject);

            createJiraTicket(config, subject, content);
        }

        inbox.close(false);
        store.close();
    }

    private static String getEmailContent(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        return "Unsupported email format";
    }

    private static void createJiraTicket(Properties config, String summary, String description)
            throws Exception {

        String jiraUrl = config.getProperty("jira.url") + "/rest/api/2/issue";

        JSONObject fields = new JSONObject();
        fields.put("summary", summary);
        fields.put("description", description);
        fields.put("issuetype", new JSONObject().put("name", config.getProperty("jira.issue.type")));
        fields.put("project", new JSONObject().put("key", config.getProperty("jira.project.key")));

        JSONObject payload = new JSONObject();
        payload.put("fields", fields);

        CloseableHttpClient client = HttpClients.createDefault();

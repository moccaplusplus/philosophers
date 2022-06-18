package smb.philosophers.canteen.report;

import com.itextpdf.html2pdf.HtmlConverter;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import smb.philosophers.Messages;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Properties;

public class Printer {

    public static final String TEMPLATE_PATH = "report.vm";

    private final VelocityEngine velocityEngine;

    public Printer() {
        final var velocityProperties = new Properties();
        velocityProperties.put("resource.loaders", "classpath");
        velocityProperties.put("resource.loader.classpath.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityProperties.put("event_handler.include.class", "org.apache.velocity.app.event.implement.IncludeRelativePath");
        velocityEngine = new VelocityEngine(velocityProperties);
    }

    public void print(Path outputPath, Map<String, Object> context) throws IOException {
        final var html = renderHtml(context);
        try (final var outputStream = Files.newOutputStream(outputPath)) {
            HtmlConverter.convertToPdf(html, outputStream);
        }
    }

    private String renderHtml(Map<String, Object> context) throws IOException {
        return renderHtml(velocityEngine.getTemplate(TEMPLATE_PATH), context);
    }

    private String renderHtml(Template template, Map<String, Object> context) throws IOException {
        try (final var writer = new StringWriter()) {
            final var velocityContext = new VelocityContext(context);
            velocityContext.put("msg", Messages.class);
            velocityContext.put("floating", new DecimalFormat("0.###"));
            velocityContext.put("decimal", new DecimalFormat("0.000"));
            template.merge(velocityContext, writer);
            return writer.toString();
        }
    }
}

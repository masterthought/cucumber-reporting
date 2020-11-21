package net.masterthought.cucumber.json;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 * @author <a href="https://github.com/itbsg">Stefan Gasterstädt</a>
 */
@RunWith(Parameterized.class)
public class EmbeddingTest {

    private static final String NO_DECODING = null;

    @Parameters(name = "\"{0}\" with \"{1}\"")
    public static Iterable<Object[]> data() {
        return asList(new Object[][] {
            { "my mime TYPE", "abc", NO_DECODING, "unknown" },
            { "mime/type", "your data", NO_DECODING, "type" },
            { "mime/type", "ZnVuY3Rpb24gbG9nZ2VyKG1lc3NhZ2UpIHsgIH0=", "function logger(message) {  }", "type" },
            { "text/xml", "some data", NO_DECODING, "embedding_-642587818.xml" },
            { "image/svg+xml", "some data", NO_DECODING, "embedding_-642587818.svg" },
        });
    }

    @Parameter(0)
    public String mimeType;

    @Parameter(1)
    public String data;

    @Parameter(2)
    public String decodedData;

    @Parameter(3)
    public String fileName;

    @Test
    public void getMimeType_ReturnsMimeType() {
        // given
        Embedding embedding = new Embedding(this.mimeType, this.data);

        // when
        String actualMimeType = embedding.getMimeType();

        // then
        assertThat(actualMimeType).isEqualTo(this.mimeType);
    }

    @Test
    public void getData_ReturnsContent() {
        // given
        Embedding embedding = new Embedding(this.mimeType, this.data);

        // when
        String actualContent = embedding.getData();

        // then
        assertThat(actualContent).isEqualTo(data);
    }

    @Test
    public void getDecodedData_ReturnsDecodedContent() {
        assumeThat(this.decodedData).isNotEqualTo(NO_DECODING);
        
        // given
        Embedding embedding = new Embedding(this.mimeType, this.data);

        // when
        String actualDecodedContent = embedding.getDecodedData();

        // then
        assertThat(actualDecodedContent).isEqualTo(this.decodedData);
    }


    @Test
    public void getFileName_ReturnsFileName() {
        assumeThat(this.fileName).contains(".");

        // given
        Embedding embedding = new Embedding(this.mimeType, this.data);

        // when
        String actualFileName = embedding.getFileName();

        // then
        assertThat(actualFileName).isEqualTo(this.fileName);
    }

    @Test
    public void getExtension__OnCommonMimeType_ReturnsFileExtension() {

        // given
        Embedding embedding = new Embedding("text/html", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("html");
    }

    @Test
    public void getExtension__OnCommonMimeTypeWithEncoding_ReturnsFileExtension() {

        // given
        Embedding embedding = new Embedding("text/html; charset=UTF-8", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("html");
    }

    @Test
    public void getExtension__OnTextMimeType_ReturnsText() {

        // given
        Embedding embedding = new Embedding("text/plain", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("txt");
    }

    @Test
    public void getExtension__OnImageUrlMimeType_ReturnsTxt() {

        // given
        Embedding embedding = new Embedding("image/url", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("image");
    }

    @Test
    public void getExtension__OnApplicationPdfMimeType_ReturnsPdf() {

        // given
        Embedding embedding = new Embedding("application/pdf", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("pdf");
    }

    @Test
    public void getExtension__OnApplicationZipMimeType_ReturnsZip() {

        // given
        Embedding embedding = new Embedding("application/zip", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("zip");
    }

    @Test
    public void getExtension__OnApplicationXTarMimeType_ReturnsTar() {

        // given
        Embedding embedding = new Embedding("application/x-tar", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("tar");
    }

    @Test
    public void getExtension__OnApplicationXBZip2MimeType_ReturnsBZ2() {

        // given
        Embedding embedding = new Embedding("application/x-bzip2", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("bz2");
    }

    @Test
    public void getExtension__OnApplicationGZipMimeType_ReturnsGZ() {

        // given
        Embedding embedding = new Embedding("application/gzip", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("gz");
    }

    @Test
    public void getExtension__OnVideoMp4MimeType_ReturnsMp4() {
        // given
        Embedding embedding = new Embedding("video/mp4", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("mp4");
    }

    @Test
    public void getExtension__OnApplicationOpenXMLFormatsOfficeDocumentSpreadsheetMimeType_ReturnsXlsx() {
        // given
        Embedding embedding = new Embedding("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xlsx");
    }

    @Test
    public void getExtension__OnApplicationVNDMsExcelMimeType_ReturnsXls() {
        // given
        Embedding embedding = new Embedding("application/vnd.ms-excel", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xls");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesPhpFromMimeType() {

        // given
        Embedding embedding = new Embedding("text/php", "echo 'Hello World!';");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("php");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesPhp7FromName() {

        // given
        Embedding embedding = new Embedding("text/php", "echo 'Hello World!';", "hello-world.php7");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("php7");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesJsFromName() {

        // given
        Embedding embedding = new Embedding("application/javascript", "alert('Hello World!');", "hello-world.js");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("js");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesXsltFromMimeType() {

        // given
        Embedding embedding = new Embedding("application/xslt+xml", "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" />");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xslt");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesXslFromName() {

        // given
        Embedding embedding = new Embedding("application/xslt+xml", "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" />", "empty-xslt.xsl");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xsl");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesXsltFromMimeType_NameNotUsable() {

        // given
        Embedding embedding = new Embedding("application/xslt+xml", "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" />", "just-some-xslt");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xslt");
    }

    @Test
    public void getExtension__OnUnknownType_DeducesXsltFromMimeType_FileExtensionNotUsable() {

        // given
        Embedding embedding = new Embedding("application/xslt+xml", "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" />", "just-some-xslt.weird_extension");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("xslt");
    }

    @Test
    public void getExtension__OnUnknownType_ReturnsUnknown() {

        // given
        Embedding embedding = new Embedding("js", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("unknown");
    }

    @Test
    public void getExtension__OnUnknownType_ReturnsUnknown_MimeTypeNotUsable() {

        // given
        Embedding embedding = new Embedding("application/vnd.tcpdump.pcap", "");

        // when
        String extension = embedding.getExtension();

        // then
        assertThat(extension).isEqualTo("unknown");
    }

    @Test
    public void getName_ReturnsName() {

        // given
        String embeddingName = "embeddingName";
        Embedding embedding = new Embedding("application/pdf", "some data", embeddingName);

        String name = embedding.getName();

        // then
        assertThat(name).isEqualTo(embeddingName);
    }
}

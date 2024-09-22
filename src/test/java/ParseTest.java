import com.github.mertakdut.Reader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class ParseTest {

    private static final Logger log = LoggerFactory.getLogger(ParseTest.class);

    private static final String TEST_EPUB = "src/test/resources/romeo-and-juliet.epub";

    @Test
    void testParsing() throws Exception {
        var reader = new Reader();
        reader.setFullContent(TEST_EPUB);
        reader.setIsIncludingTextContent(true);
        reader.setMaxContentPerSection(1000);

        Assertions.assertEquals(List.of("William Shakespeare"), reader.getInfoPackage().getMetadata().getAuthors());
    }
}

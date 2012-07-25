package dk.nsi.sdm2.core.parser;

import dk.nsi.sdm2.core.domain.AbstractRecord;
import dk.nsi.sdm2.core.persist.RecordPersister;
import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Transformer;
import org.springframework.oxm.Unmarshaller;

import javax.inject.Inject;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public abstract class SimpleParser<R extends AbstractRecord, T, E> implements Parser, Transformer<E, R> {
    @Inject
    Unmarshaller unmarshaller;

    @Override
    public void process(File dataSet, RecordPersister persister) throws ParserException {
        T types;
        try {
            types = (T) unmarshaller.unmarshal(new StreamSource(dataSet));
        } catch (IOException e) {
            throw new ParserException("Could not unmarshall", e);
        }

        Collection<R> records = CollectionUtils.collect(getTypes(types), this);
        try {
            persister.persist(records);
        } catch (SQLException e) {
            throw new ParserException("Could not persist records", e);
        }
    }

    protected abstract Collection<E> getTypes(T type);

}

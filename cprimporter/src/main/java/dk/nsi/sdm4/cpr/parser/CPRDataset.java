package dk.nsi.sdm4.cpr.parser;


import com.google.common.collect.Lists;
import dk.nsi.sdm4.core.domain.Dataset;
import dk.nsi.sdm4.core.domain.TemporalEntity;
import dk.nsi.sdm4.cpr.parser.models.*;

import java.util.Date;
import java.util.List;

public class CPRDataset
{
	@SuppressWarnings("unchecked")
	private final List<Dataset<? extends CPREntity>> datasets = Lists.newArrayList(
			new Dataset<Personoplysninger>(Personoplysninger.class),
			new Dataset<Klarskriftadresse>(Klarskriftadresse.class),
			new Dataset<NavneBeskyttelse>(NavneBeskyttelse.class),
			new Dataset<Navneoplysninger>(Navneoplysninger.class),
			new Dataset<UmyndiggoerelseVaergeRelation>(UmyndiggoerelseVaergeRelation.class),
			new Dataset<ForaeldreMyndighedRelation>(ForaeldreMyndighedRelation.class),
			new Dataset<BarnRelation>(BarnRelation.class)
	);

	private Date validFrom;

	public Date getValidFrom()
	{
		return validFrom;
	}

	public void setValidFrom(Date validFrom)
	{
		this.validFrom = validFrom;
	}

	public <T extends CPREntity> void addEntity(T entity)
	{
		entity.setDataset(this);
		for (Dataset<? extends TemporalEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entity.getClass()))
			{
				@SuppressWarnings("unchecked")
				Dataset<T> typedDataset = (Dataset<T>) dataset;
				typedDataset.add(entity);
			}
		}
	}

	public List<Dataset<? extends CPREntity>> getDatasets()
	{
		return datasets;
	}

	@SuppressWarnings("unchecked")
	public <T extends TemporalEntity> Dataset<T> getDataset(Class<T> entityClass)
	{
		for (Dataset<? extends TemporalEntity> dataset : datasets)
		{
			if (dataset.getType().equals(entityClass))
			{
				return (Dataset<T>) dataset;
			}
		}

		throw new IllegalArgumentException("Ukendt entitetsklasse: " + entityClass);
	}
}

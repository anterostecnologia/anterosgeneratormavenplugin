package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import ${importEntity};
import ${importRepository};
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;

@Repository("${repository}")
public class ${repositoryNameImpl} extends GenericSQLRepository<${entityType}, Long> implements ${repositoryName} {

	@Autowired
	public ${repositoryNameImpl}(SQLSessionFactory sessionFactory) {
		super(sessionFactory);
	}

}

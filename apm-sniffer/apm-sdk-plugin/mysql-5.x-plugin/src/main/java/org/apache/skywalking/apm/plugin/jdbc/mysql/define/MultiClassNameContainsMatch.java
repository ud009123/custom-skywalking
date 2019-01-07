package org.apache.skywalking.apm.plugin.jdbc.mysql.define;

import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.plugin.bytebuddy.AbstractJunction;
import org.apache.skywalking.apm.agent.core.plugin.match.ClassMatch;
import org.apache.skywalking.apm.agent.core.plugin.match.IndirectMatch;

import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;
import static net.bytebuddy.matcher.ElementMatchers.named;


public class MultiClassNameContainsMatch implements IndirectMatch
{
	private static final ILog logger = LogManager.getLogger(MultiClassNameContainsMatch.class);
	private List<String> matchClassNames;

	private MultiClassNameContainsMatch(String[] classNames) {
		if (classNames == null || classNames.length == 0) {
			throw new IllegalArgumentException("match class names is null");
		}
		this.matchClassNames = Arrays.asList(classNames);
	}

	@Override
	public ElementMatcher.Junction buildJunction() {
		ElementMatcher.Junction junction = not(isInterface())
				.and(
						not(isAnnotatedWith(named("javax.xml.ws.WebServiceClient")))
					.and(not(isAnnotatedWith(nameContains("javax.xml.bind.annotation"))))
				)
				.and(not(
				nameContains("jalo")
				.or(nameContains("auxiliary"))
				.or(nameContains("BySpringCGLIB"))
				.or(nameContains(".model."))
				.or(nameContains(".enums."))
				.or(nameContains("dao"))
				.or(nameContains("dtos"))
		));

		ElementMatcher.Junction junction2 = null;
		for (String name : matchClassNames) {
			if (junction2 == null) {
				junction2 = nameContains(name);
			} else {
				junction2 = junction2.or(nameContains(name));
			}
		}
		return junction.and(junction2);
	}

	@Override
	public boolean isMatch(TypeDescription typeDescription) {
		for(String classname : matchClassNames){
			if(!typeDescription.isInterface() && typeDescription.getTypeName().contains(classname)){
				return true;
			}
		}
		return false;
	}

	public static ClassMatch byMultiClassMatch(String... classNames) {
		return new MultiClassNameContainsMatch(classNames);
	}
}

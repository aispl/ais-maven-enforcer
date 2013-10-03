package pl.ais.maven.enforcer.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Verify versions of different artifact from the same group.
 */
public class DependencyGroupVersionCheck implements EnforcerRule {

	private String[] groupIds;

	@Override
	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		Log log = helper.getLog();

		try {
			MavenProject project = (MavenProject) helper.evaluate("${project}");

			Map<String, Set<Dependency>> dependencies = dispatchByGroupId(project
					.getDependencies());

			boolean result = true;
			if (groupIds != null) {
				for (String groupId : groupIds) {
					result = assertVersions(log, dependencies.get(groupId))
							&& result;
					dependencies.remove(groupId);
				}
				if (log.isDebugEnabled()) {
					if (dependencies.size() > 0) {
						for (String groupId : dependencies.keySet()) {
							int size = dependencies.get(groupId).size();
							if (size > 1) {
  							   log.debug("group " + groupId + " with " + size + " was not checked");
							}
						}
					}
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("no groupIds specified, executing for all groupIds");
				}
				for (String groupId : dependencies.keySet()) {
					result = assertVersions(log, dependencies.get(groupId))
							&& result;
				}
			}
			if (!result) {
				 throw new
				 EnforcerRuleException("incorrect group-related dependency versions");
			}

		} catch (Exception e) {
			throw new EnforcerRuleException("Unable to execute rule "
					+ e.getLocalizedMessage(), e);
		}
	}

	private boolean assertVersions(Log log, Set<Dependency> dependencies)
			throws EnforcerRuleException {
		boolean result = true;
		if (dependencies == null || dependencies.isEmpty()) {
			return result;
		}
		Iterator<Dependency> iterator = dependencies.iterator();
		Dependency first = iterator.next();
		if (log.isDebugEnabled()) {
			log.debug("checking group " + first.getGroupId() + " using " +  first.getArtifactId() + ":" + first.getVersion());
		}
		while (iterator.hasNext()) {
			Dependency next = iterator.next();
			if (log.isDebugEnabled()) {
				log.debug("  checking " + next.getArtifactId() + ":"
						+ next.getVersion());
			}
			if (!first.getVersion().equals(next.getVersion())) {
				log.warn("version mismatch for " + first.getGroupId() + ": "
						+ first.getArtifactId() + ":" + first.getVersion()
						+ " vs " + next.getArtifactId() + ":"
						+ next.getVersion());
				result = false;
			}
		}
		return result;
	}

	private Map<String, Set<Dependency>> dispatchByGroupId(
			List<Dependency> dependencies) {
		Map<String, Set<Dependency>> result = new HashMap<String, Set<Dependency>>();
		for (Dependency dep : dependencies) {
			String groupId = dep.getGroupId();
			if (result.containsKey(groupId)) {
				result.get(groupId).add(dep);
			} else {
				Set<Dependency> deps = new HashSet<Dependency>();
				deps.add(dep);
				result.put(groupId, deps);
			}
		}
		return result;
	}

	/**
	 * Returns empty string as rule is not cacheable.
	 */
	@Override
  public String getCacheId() {
		return "";
	}

	/**
	 * So far rule is not cacheable.
	 */
	@Override
  public boolean isCacheable() {
		return false;
	}

	/**
	 * Returns <code>false</code>.
	 */
	@Override
  public boolean isResultValid(EnforcerRule rule) {
		return false;
	}

	/**
	 * Sets group ids for checking.
	 *
	 * @param groupIds
	 */
	public void setGroupIds(String[] groupIds) {
		this.groupIds = groupIds;
	}
}
package org.ssmix2.jpcore.gateway.core.mapping;

import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.ArrayList;
import java.util.List;

public record MapperResult<T extends IBaseResource>(
        T resource,
        List<MappingIssue> issues
) {
    public MapperResult {
        issues = List.copyOf(issues);
    }

    public List<MappingIssue> warnings() {
        return issues.stream()
                .filter(issue -> issue.severity() == MappingIssueSeverity.WARNING)
                .toList();
    }

    public List<MappingIssue> unresolvedFields() {
        return issues.stream()
                .filter(issue -> issue.severity() == MappingIssueSeverity.UNRESOLVED_FIELD)
                .toList();
    }

    public static <T extends IBaseResource> Builder<T> builder(T resource) {
        return new Builder<>(resource);
    }

    public static final class Builder<T extends IBaseResource> {
        private final T resource;
        private final List<MappingIssue> issues = new ArrayList<>();

        private Builder(T resource) {
            this.resource = resource;
        }

        public Builder<T> warning(String field, String message) {
            issues.add(new MappingIssue(MappingIssueSeverity.WARNING, field, message));
            return this;
        }

        public Builder<T> unresolvedField(String field, String message) {
            issues.add(new MappingIssue(MappingIssueSeverity.UNRESOLVED_FIELD, field, message));
            return this;
        }

        public Builder<T> addAll(MapperResult<?> result) {
            issues.addAll(result.issues());
            return this;
        }

        public Builder<T> addAll(List<MappingIssue> mappingIssues) {
            issues.addAll(mappingIssues);
            return this;
        }

        public MapperResult<T> build() {
            return new MapperResult<>(resource, issues);
        }
    }
}

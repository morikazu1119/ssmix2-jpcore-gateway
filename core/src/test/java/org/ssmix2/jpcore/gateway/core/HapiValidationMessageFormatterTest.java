package org.ssmix2.jpcore.gateway.core;

import ca.uhn.fhir.validation.ResultSeverityEnum;
import ca.uhn.fhir.validation.SingleValidationMessage;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Test;
import org.ssmix2.jpcore.gateway.core.validation.HapiValidationMessageFormatter;
import org.ssmix2.jpcore.gateway.core.validation.ValidationIssue;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HapiValidationMessageFormatterTest {

    @Test
    void formatsHapiMessagesIntoStableValidationIssues() {
        Patient patient = new Patient();
        patient.setMeta(new Meta().addProfile("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient"));

        SingleValidationMessage message = new SingleValidationMessage();
        message.setSeverity(ResultSeverityEnum.ERROR);
        message.setLocationString("Patient.name[0]");
        message.setMessage("Family name is required");

        ValidationIssue issue = new HapiValidationMessageFormatter().format(message, patient);

        assertEquals("ERROR", issue.severity());
        assertEquals("Patient.name[0]", issue.location());
        assertEquals("Family name is required", issue.message());
        assertEquals("http://jpfhir.jp/fhir/core/StructureDefinition/JP_Patient", issue.profile());
    }
}


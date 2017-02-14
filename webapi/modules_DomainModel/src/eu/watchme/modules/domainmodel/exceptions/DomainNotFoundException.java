package eu.watchme.modules.domainmodel.exceptions;

public class DomainNotFoundException extends ValidationException {

    public DomainNotFoundException(String domainId) {
        super(String.format("Unknown domain ( %s )", domainId));
    }
}

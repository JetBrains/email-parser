package practice.email.clustering;

import practice.email.parser.UtilsKt;
import weka.core.Instances;

public class TokenEditDistance extends AbstractStringDistanceFunction {
    public TokenEditDistance() {
    }

    public TokenEditDistance(Instances data) {
        super(data);
    }

    @Override
    public String globalInfo() {
        return null;
    }

    @Override
    public String getRevision() {
        return null;
    }

    double stringDistance(String stringA, String stringB) {
        return (double) UtilsKt.getEditingDistance(stringA, stringB);
    }
}

package practice.email.clustering;

import practice.email.parser.TokenEditDistanceKt;
import weka.core.Instances;

public class TokenEditDistanceFunction extends AbstractStringDistanceFunction {
    public TokenEditDistanceFunction() {
    }

    public TokenEditDistanceFunction(Instances data) {
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
        return (double) TokenEditDistanceKt.getEditDistance(stringA, stringB);
    }
}

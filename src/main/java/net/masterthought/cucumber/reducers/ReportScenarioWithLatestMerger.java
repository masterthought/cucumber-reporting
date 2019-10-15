package net.masterthought.cucumber.reducers;

import net.masterthought.cucumber.json.Element;
import net.masterthought.cucumber.json.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Merge list of given features. If there are couple of scenarios with the same Id then
 * only the latest will be stored into the report.
 *
 * Uses when need to generate a report with rerun results of failed tests.
 */
final class ReportScenarioWithLatestMerger implements ReportFeatureMerger {

    private static final String ERROR = "You are not able to use this type of results merge. The start_timestamp field" +
            " should be part of element object. Please, update the cucumber-jvm version.";
    private static final ElementComparator ELEMENT_COMPARATOR = new ElementComparator();

    @Override
    public List<Feature> merge(List<Feature> features) {
        Map<String, Feature> mergedFeatures = new HashMap<>();
        for (Feature candidate : features) {
            Feature mergedFeature = mergedFeatures.get(candidate.getId());
            if (mergedFeature == null) {
                mergedFeatures.put(candidate.getId(), candidate);
            }
            else {
                updateElements(mergedFeatures.get(candidate.getId()), candidate.getElements());
            }
        }
        return new ArrayList<>(mergedFeatures.values());
    }

    private void updateElements(Feature feature, Element[] elements) {
        for (int i = 0; i < elements.length; i++) {
            Element current = elements[i];
            if (current.isScenario()) {
                checkArgument(current.getStartTime() != null, ERROR);
                int index = find(feature.getElements(), current);
                boolean hasBackground = isBackground(i - 1, elements);

                if (index < 0) {
                    feature.addElements(
                            hasBackground ?
                                    new Element[] {elements[i - 1], current} :
                                    new Element[] {current}
                            );
                }
                else {
                    if (replaceIfExists(feature.getElements()[index], current)) {
                        feature.getElements()[index] = current;
                        if (hasBackground && isBackground(index - 1, feature.getElements())) {
                            feature.getElements()[index - 1] = elements[i - 1];
                        }
                    }
                }
            }
        }
    }

    private boolean replaceIfExists(Element target, Element candidate) {
        return candidate.getStartTime().compareTo(target.getStartTime()) >= 0;
    }

    private boolean isBackground(int elementInd, Element[] elements) {
        return elementInd >= 0 &&
                elements != null &&
                elementInd < elements.length &&
                elements[elementInd].isBackground();
    }

    private int find(Element[] elements, Element target) {
        for (int i = 0; i < elements.length; i++) {
            if (ELEMENT_COMPARATOR.compare(elements[i], target) == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean test(List<ReducingMethod> reducingMethods) {
        return reducingMethods != null && reducingMethods.contains(ReducingMethod.MERGE_FEATURES_AND_SCENARIOS_WITH_LATEST);
    }
}

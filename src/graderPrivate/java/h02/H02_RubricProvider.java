package h02;

import h02.h1.H1_1;
import h02.h1.H1_2;
import h02.h2.H2;
import h02.h3.H3_1;
import h02.h3.H3_2;
import h02.h3.H3_3;
import h02.h3.H3_4;
import h02.h4.H4_1;
import h02.h4.H4_2;
import h02.h4.H4_3;
import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.tudalgo.algoutils.tutor.general.assertions.Context;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import static org.tudalgo.algoutils.tutor.general.jagr.RubricUtils.criterion;

public class H02_RubricProvider implements RubricProvider {

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H02 | Cleaning Convoy")
        .addChildCriteria(
            Criterion.builder()
                .shortDescription("H1 | Erstellen der Roboter-Arrays")
                .addChildCriteria(
                    Criterion.builder()
                        .shortDescription("H1.1 | ScanRobots")
                        .addChildCriteria(
                            criterion("Das Array hat die korrekte Länge der Breite der Welt.",
                                JUnitTestRef.ofMethod(() -> H1_1.class.getDeclaredMethod("testArrayLength", TestUtils.WorldSize.class, Context.class))),
                            criterion("Es stehen auf keinem Feld zwei Roboter.",
                                JUnitTestRef.ofMethod(() -> H1_1.class.getDeclaredMethod("testNoRobotsWithDuplicatePositions", TestUtils.WorldSize.class, Context.class))),
                            criterion("Die Roboter sind alle richtig initialisiert (Koordinaten, Ausrichtung und keine Münzen).",
                                JUnitTestRef.ofMethod(() -> H1_1.class.getDeclaredMethod("testRobotStates", TestUtils.WorldSize.class, Context.class)))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H1.2 | CleanRobots")
                        .addChildCriteria(
                            criterion("Das Array hat die korrekte Länge der Breite der Welt.",
                                JUnitTestRef.ofMethod(() -> H1_2.class.getDeclaredMethod("testArrayLength", TestUtils.WorldSize.class, Context.class))),
                            criterion("Es stehen auf keinem Feld zwei Roboter.",
                                JUnitTestRef.ofMethod(() -> H1_2.class.getDeclaredMethod("testNoRobotsWithDuplicatePositions", TestUtils.WorldSize.class, Context.class))),
                            criterion("Die Roboter sind alle richtig initialisiert (Koordinaten, Ausrichtung und keine Münzen).",
                                JUnitTestRef.ofMethod(() -> H1_2.class.getDeclaredMethod("testRobotStates", TestUtils.WorldSize.class, Context.class)))
                        )
                        .build()
                )
                .build(),
            Criterion.builder()
                .shortDescription("H2 | Platzieren der Münzen")
                .addChildCriteria(
                    criterion("Es werden auf die korrekten Felder Münzen platziert.",
                        JUnitTestRef.ofMethod(() -> H2.class.getDeclaredMethod("testCoinPositions", TestUtils.WorldSize.class, Context.class)), 2),
                    criterion("Es wird die korrekte Anzahl an Münzen auf dem jeweiligen Feld platziert",
                        JUnitTestRef.ofMethod(() -> H2.class.getDeclaredMethod("testCoinAmounts", TestUtils.WorldSize.class, Context.class)))
                )
                .build(),
            Criterion.builder()
                .shortDescription("H3 | Flip and rotate")
                .addChildCriteria(
                    Criterion.builder()
                        .shortDescription("H3.1 | Invertierung der Roboter")
                        .addChildCriteria(
                            criterion("Die Roboter sind im Array korrekt vertauscht.", 2,
                                JUnitTestRef.ofMethod(() -> H3_1.class.getDeclaredMethod("testInvertRobotsArray"))),
                            criterion("Das Element enthält die gleichen Elemente wie vor dem Aufruf.", 1,
                                JUnitTestRef.ofMethod(() -> H3_1.class.getDeclaredMethod("testArrayHasSameElements"))),
                            criterion("Die Roboter wurden nicht modifiziert.", 1,
                                JUnitTestRef.ofMethod(() -> H3_1.class.getDeclaredMethod("testRobotsWereNotModified")))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H3.2 | Rotation der Roboter")
                        .addChildCriteria(
                            criterion("Die Roboter im Array gucken alle in die entgegengesetzte Richtung.", 2,
                                JUnitTestRef.ofMethod(() -> H3_2.class.getDeclaredMethod("testRotateRobots", TestUtils.WorldSize.class, Context.class))),
                            criterion("Die Methode `checkForDamage` wird für jeden Roboter einmal aufgerufen.", 1,
                                JUnitTestRef.ofMethod(() -> H3_2.class.getDeclaredMethod("testCheckForDamageCalls", TestUtils.WorldSize.class, Context.class)))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H3.3 | Verschleiß behandeln")
                        .addChildCriteria(
                            criterion("Es werden nur ausgeschaltete Roboter ersetzt.",
                                JUnitTestRef.ofMethod(() -> H3_3.class.getDeclaredMethod("testOnlyDisabledRobotsAreReplaced", TestUtils.WorldSize.class, Context.class))),
                            criterion("Es wird korrekt ein neuer Roboter erstellt (korrekte Ausrichtung, Koordinaten und Coins).",
                                JUnitTestRef.ofMethod(() -> H3_3.class.getDeclaredMethod("testReplacedRobotsHaveCorrectProperties", TestUtils.WorldSize.class, Context.class))),
                            criterion("Es wird der korrekte Typ von Robot für das entsprechende Array erstellt.",
                                JUnitTestRef.ofMethod(() -> H3_3.class.getDeclaredMethod("testReplacedRobotsHaveCorrectType", TestUtils.WorldSize.class, Context.class)))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H3.4 | spinning Robots")
                        .addChildCriteria(
                            criterion("Die Methode funktioniert vollständig korrekt.", JUnitTestRef.ofClass(H3_4.class))
                        )
                        .build()
                )
                .build(),
            Criterion.builder()
                .shortDescription("H4 | Kolonne marsch!")
                .addChildCriteria(
                    Criterion.builder()
                        .shortDescription("H4.1 | Die heimkehrenden Helden")
                        .addChildCriteria(
                            criterion("Die Roboter werden korrekt an das Ende der Welt bewegt.",
                                JUnitTestRef.ofMethod(() -> H4_1.class.getDeclaredMethod("testRobotsFinalPosition", TestUtils.WorldSize.class, Context.class))),
                            criterion("Die Roboter machen keine andere Aktion.",
                                JUnitTestRef.ofMethod(() -> H4_1.class.getDeclaredMethod("testRobotsPerformedOnlyMoveActions", TestUtils.WorldSize.class, Context.class)))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H4.2 | Scannen der Welt")
                        .addChildCriteria(
                            criterion("Das boolean-Array wurde korrekt erstellt.",
                                JUnitTestRef.ofMethod(() -> H4_2.class.getDeclaredMethod("testArrayDimensions", JsonParameterSet.class))),
                            criterion("Das boolean-Array enthält an den Positionen von Münzen `true` und an allen anderen Positionen `false`.",
                                JUnitTestRef.ofMethod(() -> H4_2.class.getDeclaredMethod("testResultArrayEntriesCorrect", JsonParameterSet.class))),
                            criterion("Die Methode spinRobots wird korrekt verwendet.",
                                JUnitTestRef.ofMethod(() -> H4_2.class.getDeclaredMethod("testSpinRobotsUsage", JsonParameterSet.class))),
                            criterion("Die Methode returnRobots wird korrekt verwendet.",
                                JUnitTestRef.ofMethod(() -> H4_2.class.getDeclaredMethod("testReturnRobotsUsage", JsonParameterSet.class))),
                            criterion("Die Roboter stehen nach Ende der Methode wieder an der richtigen Position und sind richtig ausgerichtet.",
                                JUnitTestRef.ofMethod(() -> H4_2.class.getDeclaredMethod("testRobotsFinalPositionAndDirection", JsonParameterSet.class)))
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H4.3 | Bewegung der Putzkolonne")
                        .addChildCriteria(
                            criterion("Es werden an den richtigen Positionen Münzen aufgesammelt.",
                                JUnitTestRef.ofMethod(() -> H4_3.class.getDeclaredMethod("testPickedUpCoinAmountsAny", JsonParameterSet.class))),
                            criterion("Es wird immer nur eine Münze von einem Feld aufgehoben.",
                                JUnitTestRef.ofMethod(() -> H4_3.class.getDeclaredMethod("testPickedUpCoinAmountsExact", JsonParameterSet.class))),
                            criterion("Die Roboter bewegen sich korrekt an das Ende der Welt.",
                                JUnitTestRef.ofMethod(() -> H4_3.class.getDeclaredMethod("testRobotMovement", JsonParameterSet.class))),
                            criterion("Die Methode spinRobots wird korrekt verwendet.",
                                JUnitTestRef.ofMethod(() -> H4_3.class.getDeclaredMethod("testSpinRobotsUsage", JsonParameterSet.class))),
                            criterion("Die Methode returnRobots wird korrekt verwendet.",
                                JUnitTestRef.ofMethod(() -> H4_3.class.getDeclaredMethod("testReturnRobotsUsage", JsonParameterSet.class)))
                        )
                        .build()
                )
                .build()
        )
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}

package tools.vitruv.stoex.interpreter.visitors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import tools.vitruv.stoex.interpreter.TypeEnum;
import tools.vitruv.stoex.stoex.Expression;
import tools.vitruv.stoex.tests.StoexInjectorProvider;

@ExtendWith(InjectionExtension.class)
@InjectWith(StoexInjectorProvider.class)
@DisplayName("Type Inference Visitor Tests")
class TypeInferenceVisitorTest {

    @Inject
    private ParseHelper<Expression> parseHelper;

    private TypeInferenceVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new TypeInferenceVisitor();
    }

    @Nested
    @DisplayName("Basic Literal Types")
    class BasicLiteralTests {

        @Test
        @DisplayName("Should infer INT type for integer literals")
        void testIntegerLiteral() throws Exception {
            Expression expr = parseHelper.parse("42");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.INT, result);
        }

        @Test
        @DisplayName("Should infer DOUBLE type for double literals")
        void testDoubleLiteral() throws Exception {
            Expression expr = parseHelper.parse("3.14");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should infer BOOL type for boolean literals")
        void testBooleanLiteral() throws Exception {
            Expression expr = parseHelper.parse("true");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL, result);

            expr = parseHelper.parse("false");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL, result);
        }

        @Test
        @DisplayName("Should infer STRING type for string literals")
        void testStringLiteral() throws Exception {
            Expression expr = parseHelper.parse("\"hello\"");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.STRING, result);
        }
    }

    @Nested
    @DisplayName("Distribution Types")
    class DistributionTests {

        @Test
        @DisplayName("Should infer BERNOULLI_PMF for Bernoulli distribution")
        void testBernoulliDistribution() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BERNOULLI_PMF, result);
        }

        @Test
        @DisplayName("Should reject non-numeric Bernoulli parameter")
        void testBernoulliInvalidParameter() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(\"invalid\")");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should infer BINOMIAL_PMF for Binomial distribution")
        void testBinomialDistribution() throws Exception {
            Expression expr = parseHelper.parse("Binomial(10, 0.3)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BINOMIAL_PMF, result);
        }

        @Test
        @DisplayName("Should reject non-integer n in Binomial distribution")
        void testBinomialInvalidN() throws Exception {
            Expression expr = parseHelper.parse("Binomial(3.5, 0.3)");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should reject non-numeric p in Binomial distribution")
        void testBinomialInvalidP() throws Exception {
            Expression expr = parseHelper.parse("Binomial(10, \"invalid\")");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should infer POISSON_PMF for Poisson distribution")
        void testPoissonDistribution() throws Exception {
            Expression expr = parseHelper.parse("Poisson(2.5)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.POISSON_PMF, result);
        }

        @Test
        @DisplayName("Should reject non-numeric lambda in Poisson distribution")
        void testPoissonInvalidLambda() throws Exception {
            Expression expr = parseHelper.parse("Poisson(\"invalid\")");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should infer NORMAL_PDF for Normal distribution")
        void testNormalDistribution() throws Exception {
            Expression expr = parseHelper.parse("Normal(0.0, 1.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.NORMAL_PDF, result);
        }

        @Test
        @DisplayName("Should reject non-numeric parameters in Normal distribution")
        void testNormalInvalidParameters() throws Exception {
            Expression expr1 = parseHelper.parse("Normal(\"invalid\", 1.0)");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr1));

            Expression expr2 = parseHelper.parse("Normal(0.0, \"invalid\")");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr2));
        }

        @Test
        @DisplayName("Should infer EXPONENTIAL_PDF for Exponential distribution")
        void testExponentialDistribution() throws Exception {
            Expression expr = parseHelper.parse("Exponential(1.5)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.EXPONENTIAL_PDF, result);
        }

        @Test
        @DisplayName("Should reject non-numeric rate in Exponential distribution")
        void testExponentialInvalidRate() throws Exception {
            Expression expr = parseHelper.parse("Exponential(\"invalid\")");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticTests {

        @Test
        @DisplayName("Should infer INT for integer addition")
        void testIntegerAddition() throws Exception {
            Expression expr = parseHelper.parse("5 + 3");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.INT, result);
        }

        @Test
        @DisplayName("Should infer DOUBLE for mixed number operations")
        void testMixedNumberOperations() throws Exception {
            Expression expr = parseHelper.parse("5 + 3.0");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);

            expr = parseHelper.parse("5.0 + 3");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should infer DOUBLE_PMF for discrete distribution operations")
        void testDiscreteDistributionArithmetic() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) + Poisson(2.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PMF, result);
        }

        @Test
        @DisplayName("Should infer DOUBLE_PDF for continuous distribution operations")
        void testContinuousDistributionArithmetic() throws Exception {
            Expression expr = parseHelper.parse("Normal(0.0, 1.0) + Exponential(1.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);
        }

        @Test
        @DisplayName("Should infer DOUBLE_PDF for mixed distribution operations")
        void testMixedDistributionArithmetic() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) + Normal(0.0, 1.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);
        }

        @Test
        @DisplayName("Should preserve distribution type when adding constant")
        void testDistributionPlusConstant() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) + 5");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BERNOULLI_PMF, result);

            expr = parseHelper.parse("Normal(0.0, 1.0) + 2.5");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.NORMAL_PDF, result);
        }

        @Test
        @DisplayName("Should handle multiplication operations")
        void testMultiplicationOperations() throws Exception {
            Expression expr = parseHelper.parse("3 * 4");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.INT, result);

            expr = parseHelper.parse("3.0 * 4");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should handle distribution multiplication")
        void testDistributionMultiplication() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) * 2");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BERNOULLI_PMF, result);

            expr = parseHelper.parse("Bernoulli(0.5) * Poisson(1.0)");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PMF, result);
        }
    }

    @Nested
    @DisplayName("Power Operations")
    class PowerTests {

        @Test
        @DisplayName("Should infer DOUBLE for numeric power operations")
        void testNumericPower() throws Exception {
            Expression expr = parseHelper.parse("2 ^ 3");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should preserve distribution type for distribution power")
        void testDistributionPower() throws Exception {
            Expression expr = parseHelper.parse("Normal(0.0, 1.0) ^ 2");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.NORMAL_PDF, result);
        }

        @Test
        @DisplayName("Should reject non-numeric exponent for distributions")
        void testDistributionInvalidExponent() throws Exception {
            Expression expr = parseHelper.parse("Normal(0.0, 1.0) ^ \"invalid\"");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }
    }

    @Nested
    @DisplayName("Comparison Operations")
    class ComparisonTests {

        @Test
        @DisplayName("Should infer BOOL for numeric comparisons")
        void testNumericComparisons() throws Exception {
            Expression expr = parseHelper.parse("5 > 3");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL, result);

            expr = parseHelper.parse("5.0 <= 3.0");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL, result);

            expr = parseHelper.parse("5 == 5");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL, result);
        }

        @Test
        @DisplayName("Should infer BOOL_PMF for distribution comparisons")
        void testDistributionComparisons() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) > 0");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL_PMF, result);

            expr = parseHelper.parse("5 < Normal(0.0, 1.0)");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL_PMF, result);
        }
    }

    @Nested
    @DisplayName("If-Else Operations")
    class IfElseTests {

        @Test
        @DisplayName("Should infer branch type for boolean condition")
        void testBooleanCondition() throws Exception {
            Expression expr = parseHelper.parse("true ? 5 : 3");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.INT, result);

            expr = parseHelper.parse("false ? 5.0 : 3");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should promote types for different branch types")
        void testTypePromotion() throws Exception {
            Expression expr = parseHelper.parse("true ? 5 : 3.0");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE, result);
        }

        @Test
        @DisplayName("Should handle distribution conditions")
        void testDistributionCondition() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) ? 5 : 3");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.INT, result);
        }

        @Test
        @DisplayName("Should reject non-boolean conditions")
        void testInvalidCondition() throws Exception {
            Expression expr = parseHelper.parse("5 ? 1 : 2");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should handle distribution branches")
        void testDistributionBranches() throws Exception {
            Expression expr = parseHelper.parse("true ? Bernoulli(0.5) : Poisson(1.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PMF, result);

            expr = parseHelper.parse("true ? Normal(0.0, 1.0) : Exponential(1.0)");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);

            expr = parseHelper.parse("true ? Bernoulli(0.5) : Normal(0.0, 1.0)");
            result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);
        }
    }

    @Nested
    @DisplayName("Type Annotation Storage")
    class AnnotationTests {

        @Test
        @DisplayName("Should store and retrieve type annotations")
        void testTypeAnnotationStorage() throws Exception {
            Expression expr = parseHelper.parse("42");
            TypeEnum result = visitor.doSwitch(expr);

            assertNotNull(result);
            assertEquals(TypeEnum.INT, result);
            assertEquals(TypeEnum.INT, visitor.getTypeAnnotation(expr));
        }

        @Test
        @DisplayName("Should store annotations for complex expressions")
        void testComplexExpressionAnnotations() throws Exception {
            Expression expr = parseHelper.parse("5 + 3");
            TypeEnum result = visitor.doSwitch(expr);

            assertNotNull(result);
            assertEquals(TypeEnum.INT, result);
            assertEquals(TypeEnum.INT, visitor.getTypeAnnotation(expr));
        }

        @Test
        @DisplayName("Should manually set and get annotations")
        void testManualAnnotations() throws Exception {
            Expression expr = parseHelper.parse("42");
            visitor.setTypeAnnotation(expr, TypeEnum.DOUBLE);

            assertEquals(TypeEnum.DOUBLE, visitor.getTypeAnnotation(expr));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorTests {

        @Test
        @DisplayName("Should handle invalid arithmetic operations")
        void testInvalidArithmetic() throws Exception {
            Expression expr = parseHelper.parse("\"hello\" + 5");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should handle invalid power operations")
        void testInvalidPower() throws Exception {
            Expression expr = parseHelper.parse("\"hello\" ^ 2");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }

        @Test
        @DisplayName("Should handle incompatible type promotion")
        void testIncompatiblePromotion() throws Exception {
            Expression expr = parseHelper.parse("true ? \"hello\" : 5");
            assertThrows(RuntimeException.class, () -> visitor.doSwitch(expr));
        }
    }

    @Nested
    @DisplayName("Complex Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should handle nested arithmetic with distributions")
        void testNestedDistributionArithmetic() throws Exception {
            Expression expr = parseHelper.parse("(Bernoulli(0.5) + 1) * (Normal(0.0, 1.0) + 2.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);
        }

        @Test
        @DisplayName("Should handle complex if-else with distributions")
        void testComplexIfElseWithDistributions() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.7) ? (Normal(0.0, 1.0) + 1.0) : (Exponential(1.0) * 2.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PDF, result);
        }

        @Test
        @DisplayName("Should handle power of distribution comparison")
        void testPowerDistributionComparison() throws Exception {
            Expression expr = parseHelper.parse("(Normal(0.0, 1.0) ^ 2) > 0.5");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.BOOL_PMF, result);
        }

        @Test
        @DisplayName("Should handle mixed operations with multiple distribution types")
        void testMixedMultipleDistributions() throws Exception {
            Expression expr = parseHelper.parse("Bernoulli(0.5) + Binomial(10, 0.3) * Poisson(2.0)");
            TypeEnum result = visitor.doSwitch(expr);
            assertEquals(TypeEnum.DOUBLE_PMF, result);
        }
    }
}

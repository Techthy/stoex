package tools.vitruv.stoex.interpreter;

public enum TypeEnum {
    // Basic types
    INT, BOOL, DOUBLE, ENUM, STRING,

    // Discrete probability distributions (PMF)
    BERNOULLI_PMF,
    BINOMIAL_PMF,
    POISSON_PMF,
    GEOMETRIC_PMF,
    DISCRETE_UNIFORM_PMF,

    // Generic PMF types (for mixed operations)
    INT_PMF,
    DOUBLE_PMF,
    ENUM_PMF,
    BOOL_PMF,
    ANY_PMF,

    // Continuous probability distributions (PDF)
    NORMAL_PDF,
    EXPONENTIAL_PDF,
    GAMMA_PDF,
    LOGNORMAL_PDF,
    UNIFORM_PDF,
    BETA_PDF,

    // Generic PDF types
    DOUBLE_PDF,
    CONTINUOUS_PROBFUNCTION,

    // Special types
    AUX_FUNCTION,
    ANY
}
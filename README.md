# Stoex Language Interpreter

A complete interpreter for the Stoex (Stochastic Expressions) language, a domain-specific language for mathematical and probabilistic computations. This interpreter provides the same functionality as the Palladio Stoex analyzer.

## Features

The Stoex interpreter supports a comprehensive range of operations:

### Basic Data Types
- **Integers**: `42`, `-10`
- **Doubles**: `3.14`, `-2.5e10`
- **Booleans**: `true`, `false`
- **Strings**: `"hello world"`

### Arithmetic Operations
- **Addition**: `5 + 3`
- **Subtraction**: `10 - 4`
- **Multiplication**: `6 * 7`
- **Division**: `15 / 3`
- **Modulo**: `17 % 5`
- **Power**: `2 ^ 3`

### Boolean Operations
- **AND**: `true AND false`
- **OR**: `true OR false`
- **XOR**: `true XOR false`
- **NOT**: `NOT true`

### Comparison Operations
- **Greater than**: `5 > 3`
- **Less than**: `3 < 5`
- **Equal**: `5 == 5`
- **Not equal**: `5 <> 3`
- **Greater or equal**: `5 >= 3`
- **Less or equal**: `3 <= 5`

### Control Flow
- **If-else expressions**: `condition ? trueValue : falseValue`
- **Complex conditions**: `(x > 0 AND y > 0) ? "positive" : "negative"`

### Variables
- Runtime variable support with Maps
- Persistent variable management
- Built-in constants: `PI`, `E`

### Built-in Functions
- **Mathematical functions**: `sin(x)`, `cos(x)`, `sqrt(x)`, `abs(x)`
- **Utility functions**: `max(a, b)`, `min(a, b)`

### Probability Distributions

#### Discrete Distributions
- **Bernoulli**: `Bernoulli(p)` - success probability p
- **Binomial**: `Binomial(n, p)` - n trials, success probability p
- **Poisson**: `Poisson(lambda)` - rate parameter lambda
- **Geometric**: `Geometric(p)` - success probability p

#### Continuous Distributions
- **Normal**: `Normal(mu, sigma)` - mean mu, standard deviation sigma
- **Exponential**: `Exponential(rate)` - rate parameter
- **Uniform**: `Uniform(a, b)` - uniform between a and b
- **Gamma**: `Gamma(alpha, theta)` - shape alpha, scale theta
- **Beta**: `Beta(alpha, beta)` - shape parameters

### Type System
- Static type inference and validation
- Runtime type checking
- Distribution type system (PMF/PDF types)

## Quick Start

### High-Level API (Recommended)

```java
import tools.vitruv.stoex.interpreter.StoexEvaluator;
import java.util.Map;
import java.util.HashMap;

// Create evaluator
StoexEvaluator evaluator = new StoexEvaluator();

// Basic expressions
Object result = evaluator.evaluate("2 + 3 * 4");  // Returns 14.0

// With variables
Map<String, Object> vars = new HashMap<>();
vars.put("x", 10.0);
vars.put("y", 5.0);
Object result = evaluator.evaluate("x + y", vars);  // Returns 15.0

// Persistent variables
evaluator.setVariable("radius", 5.0);
Object area = evaluator.evaluate("PI * radius ^ 2");  // Returns ~78.54

// Type inference
TypeEnum type = evaluator.inferType("Normal(0, 1)");  // Returns NORMAL_PDF

// Probability distributions
Object sample = evaluator.evaluate("Normal(0, 1)");  // Random sample from standard normal
```

### Low-Level API (Advanced)

```java
import tools.vitruv.stoex.interpreter.visitors.ExpressionEvaluationVisitor;
import tools.vitruv.stoex.StoexStandaloneSetup;
// ... Xtext setup code ...

ExpressionEvaluationVisitor visitor = new ExpressionEvaluationVisitor();
visitor.setVariable("x", 10);
Expression expr = parseExpression("x * 2");
Object result = visitor.doSwitch(expr);
```

## Running Demos

### Complete Integration Demo
Shows all features with practical examples:
```bash
mvn test -Dtest=StoexEvaluatorShowcaseTest -q
```

### Low-Level API Demo
Demonstrates direct visitor usage:
```bash
mvn test -Dtest=ExpressionEvaluationDemoTest -q
```

### All Tests
Run the complete test suite:
```bash
mvn clean verify
```

## Building

### Prerequisites
- Java 17+
- Maven 3.6+

### Build Commands
```bash
# Clean and compile
mvn clean compile

# Run all tests
mvn test

# Full build with verification
mvn clean verify
```

## Example Use Cases

### Performance Monitoring
```java
Map<String, Object> metrics = Map.of(
    "cpuUsage", 0.75,
    "memUsage", 0.60,
    "threshold", 0.80
);

// CPU alert check
Boolean alert = (Boolean) evaluator.evaluate("cpuUsage > threshold", metrics);

// Health score calculation
Double health = (Double) evaluator.evaluate("(1.0 - cpuUsage) * 100", metrics);
```

### Reliability Analysis
```java
Map<String, Object> components = Map.of(
    "componentA", 0.99,
    "componentB", 0.95,
    "componentC", 0.98
);

// Series system reliability
Double seriesReliability = (Double) evaluator.evaluate(
    "componentA * componentB * componentC", components);

// Parallel system reliability  
Double parallelReliability = (Double) evaluator.evaluate(
    "1.0 - (1.0 - componentA) * (1.0 - componentB)", components);
```

### Probabilistic Modeling
```java
Map<String, Object> params = Map.of(
    "failureRate", 0.01,
    "repairTime", 2.0
);

// Sample failure time
Double failureTime = (Double) evaluator.evaluate("Exponential(failureRate)", params);

// Calculate availability
Double availability = (Double) evaluator.evaluate(
    "1.0 / (1.0 + failureRate * repairTime)", params);
```

## Test Coverage

**83 tests, all passing ✅**

The interpreter includes comprehensive test coverage:

- ✅ **Basic Operations**: All arithmetic, boolean, and comparison operations
- ✅ **Variables**: Runtime variables, persistent variables, type context
- ✅ **Functions**: All built-in mathematical functions
- ✅ **Distributions**: All probability distributions with sampling
- ✅ **Type System**: Static type inference and validation
- ✅ **Integration**: High-level StoexEvaluator API
- ✅ **Error Handling**: Graceful error reporting
- ✅ **Complex Scenarios**: Real-world use case examples

## Architecture

The interpreter consists of several key components:

1. **StoexEvaluator**: High-level integration API
2. **ExpressionEvaluationVisitor**: Core evaluation engine using visitor pattern
3. **TypeInferenceVisitor**: Static type analysis and validation
4. **Xtext Parser**: Grammar-based parsing to AST
5. **Distribution Sampling**: Random sampling from probability distributions

### Design Patterns
- **Visitor Pattern**: For AST traversal and evaluation
- **Strategy Pattern**: For distribution sampling
- **Builder Pattern**: For expression parsing setup

## Advanced Features

### Type System
```java
// Static type inference
evaluator.inferType("2 + 3");              // INT
evaluator.inferType("2.5 + 1.5");          // DOUBLE
evaluator.inferType("Bernoulli(0.5)");     // BERNOULLI_PMF
evaluator.inferType("Normal(0, 1)");       // NORMAL_PDF
```

### Distribution Operations
```java
// Distribution arithmetic creates new distributions
evaluator.evaluate("Normal(0, 1) + Normal(0, 1)");  // Convolution
evaluator.evaluate("Normal(0, 1) * 2");             // Scaling
```

### Variable Context Management
```java
// Persistent variables
evaluator.setVariable("globalParam", 1.5);

// Runtime variables (temporary)
Map<String, Object> tempVars = Map.of("localParam", 2.0);
evaluator.evaluate("globalParam + localParam", tempVars);

// Variables are properly typed
evaluator.setVariable("count", 42);        // Integer
evaluator.setVariable("rate", 0.05);       // Double
evaluator.setVariable("active", true);     // Boolean
```

## Grammar Reference

### Operator Precedence
1. Parentheses `()`
2. Power `^`
3. Unary operators `-`, `NOT`
4. Multiplication, Division, Modulo `*`, `/`, `%`
5. Addition, Subtraction `+`, `-`
6. Comparisons `>`, `<`, `==`, `<>`, `>=`, `<=`
7. Boolean AND `AND`
8. Boolean OR, XOR `OR`, `XOR`
9. Conditional `? :`

### Distribution Syntax
```stoex
# Discrete distributions
Bernoulli(0.5)
Binomial(10, 0.3)
Poisson(2.5)

# Continuous distributions  
Normal(0, 1)
Exponential(0.1)
Uniform(0, 10)
```

## Integration with Existing Systems

This interpreter is designed to be compatible with Palladio Stoex analyzer and can be integrated into:

- Performance modeling tools
- Reliability analysis systems
- Monte Carlo simulation frameworks
- Probabilistic programming environments

## Contributing

The codebase is well-structured for extensions:

- Add new distributions in the grammar and visitor
- Extend built-in functions in ExpressionEvaluationVisitor
- Add new operators by extending the grammar
- Implement custom type checking rules

## License

[Add your license information here]

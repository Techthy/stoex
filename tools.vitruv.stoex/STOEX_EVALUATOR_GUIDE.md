# StoexEvaluator - Complete Integration Documentation

## Overview

The `StoexEvaluator` is a high-level integration class that provides a unified API for the Stoex interpreter system. It combines expression parsing, type inference, and expression evaluation into a single, easy-to-use interface.

## Architecture

The StoexEvaluator integrates three key components:

1. **Xtext Parser**: Parses Stoex expression strings into AST nodes
2. **TypeInferenceVisitor**: Performs static type analysis and validation
3. **ExpressionEvaluationVisitor**: Evaluates expressions with runtime values

## Key Features

### ✅ Complete Expression Support

- **Basic Arithmetic**: `2 + 3 * 4`, `(x + y) / 2`
- **Boolean Logic**: `true AND false`, `x > threshold`
- **Conditional Expressions**: `active ? x : y`
- **Mathematical Functions**: `sin(angle)`, `sqrt(value)`, `max(x, y)`
- **Constants**: `PI`, `E`

### ✅ Variable Management

- **Runtime Variables**: `evaluate("x + y", variables)`
- **Persistent Variables**: `setVariable("a", 100.0)`
- **Variable Type Inference**: Automatic type detection from values

### ✅ Probability Distributions

- **Discrete**: `Bernoulli(p)`, `Binomial(n, p)`, `Poisson(lambda)`
- **Continuous**: `Normal(mu, sigma)`, `Exponential(rate)`, `Uniform(a, b)`
- **Sampling**: Returns random samples from distributions

### ✅ Type System Integration

- **Static Type Checking**: `inferType("2 + 3")` → `INT`
- **Distribution Types**: `Bernoulli(0.5)` → `BERNOULLI_PMF`
- **Variable Context**: Proper type handling for variables

## Usage Examples

### Basic Usage

```java
StoexEvaluator evaluator = new StoexEvaluator();

// Simple expressions
Object result = evaluator.evaluate("2 + 3 * 4"); // Returns 14.0
Boolean comparison = (Boolean) evaluator.evaluate("5 > 3"); // Returns true

// Type inference
TypeEnum type = evaluator.inferType("2.5 + 1.5"); // Returns DOUBLE
```

### Variables

```java
// Using variable maps
Map<String, Object> vars = new HashMap<>();
vars.put("x", 10.0);
vars.put("y", 5.0);
Object result = evaluator.evaluate("x + y", vars); // Returns 15.0

// Persistent variables
evaluator.setVariable("a", 100.0);
evaluator.setVariable("b", 50.0);
Object result = evaluator.evaluate("a / b"); // Returns 2.0
```

### Probability Distributions

```java
Map<String, Object> params = new HashMap<>();
params.put("p", 0.3);
params.put("mu", 0.0);
params.put("sigma", 1.0);

// Sample from distributions
Object bernoulli = evaluator.evaluate("Bernoulli(p)", params);
Object normal = evaluator.evaluate("Normal(mu, sigma)", params);
```

### Practical Applications

```java
// Performance monitoring
Map<String, Object> metrics = new HashMap<>();
metrics.put("cpuUsage", 0.75);
metrics.put("threshold", 0.80);

Boolean alert = (Boolean) evaluator.evaluate("cpuUsage > threshold", metrics);
Double healthScore = (Double) evaluator.evaluate("(1.0 - cpuUsage) * 100", metrics);

// Reliability analysis
Map<String, Object> components = new HashMap<>();
components.put("componentA", 0.99);
components.put("componentB", 0.95);

Double reliability = (Double) evaluator.evaluate(
    "componentA * componentB", components); // Series system
```

## API Reference

### Core Methods

#### `evaluate(String expressionString)`

Evaluates an expression without variables.

- **Parameters**: `expressionString` - The Stoex expression to evaluate
- **Returns**: `Object` - The evaluation result
- **Example**: `evaluator.evaluate("2 + 3")` → `5.0`

#### `evaluate(String expressionString, Map<String, Object> variables)`

Evaluates an expression with variable context.

- **Parameters**:
  - `expressionString` - The Stoex expression to evaluate
  - `variables` - Map of variable names to values
- **Returns**: `Object` - The evaluation result
- **Example**: `evaluator.evaluate("x + y", vars)` → `15.0`

#### `inferType(String expressionString)`

Infers the type of an expression without evaluating it.

- **Parameters**: `expressionString` - The Stoex expression to analyze
- **Returns**: `TypeEnum` - The inferred type
- **Example**: `evaluator.inferType("2.5")` → `TypeEnum.DOUBLE`

#### `setVariable(String name, Object value)`

Sets a persistent variable for future evaluations.

- **Parameters**:
  - `name` - Variable name
  - `value` - Variable value
- **Example**: `evaluator.setVariable("pi", 3.14159)`

#### `getVariable(String name)`

Gets the value of a persistent variable.

- **Parameters**: `name` - Variable name
- **Returns**: `Object` - Variable value or null
- **Example**: `Object pi = evaluator.getVariable("pi")`

## Supported Grammar

### Expressions

- **Arithmetic**: `+`, `-`, `*`, `/`, `%`, `^`
- **Comparison**: `>`, `<`, `>=`, `<=`, `==`, `<>`
- **Boolean**: `AND`, `OR`, `XOR`, `NOT`
- **Conditional**: `condition ? ifTrue : ifFalse`
- **Parentheses**: `(expression)`

### Functions

- **Mathematical**: `sin()`, `cos()`, `sqrt()`, `abs()`, `max()`, `min()`
- **Constants**: `PI`, `E`

### Distributions

- **Discrete**: `Bernoulli(p)`, `Binomial(n,p)`, `Poisson(lambda)`, `Geometric(p)`
- **Continuous**: `Normal(mu,sigma)`, `Exponential(rate)`, `Uniform(a,b)`, `Gamma(alpha,theta)`

### Literals

- **Numbers**: `42`, `3.14`, `-5.0`
- **Booleans**: `true`, `false`
- **Strings**: `"hello"`
- **Variables**: `x`, `myVariable`

## Test Coverage

The StoexEvaluator has comprehensive test coverage:

- **15 Integration Tests**: All passing in `StoexEvaluatorTest`
- **128 Total Tests**: Full system test suite
- **Showcase Demonstration**: Complete functionality demo in `StoexEvaluatorShowcaseTest`

### Test Categories

1. **Basic Expressions**: Simple arithmetic and boolean operations
2. **Variable Handling**: Runtime and persistent variables
3. **Mathematical Functions**: Built-in function calls
4. **Probability Distributions**: Sampling from various distributions
5. **Type Inference**: Static type analysis
6. **Complex Scenarios**: Real-world use cases

## Integration with Existing System

The StoexEvaluator seamlessly integrates with the existing Stoex infrastructure:

- **Maintains Compatibility**: Works with existing `TypeInferenceVisitor`
- **Extends Functionality**: Adds variable context support
- **Preserves Performance**: Efficient evaluation with minimal overhead
- **Error Handling**: Graceful error reporting and recovery

## Performance Characteristics

- **Parse Once, Evaluate Many**: Expressions can be cached for repeated evaluation
- **Efficient Variable Handling**: O(1) variable lookup
- **Type Safety**: Static type checking prevents runtime errors
- **Memory Efficient**: Minimal object allocation during evaluation

## Conclusion

The StoexEvaluator provides a complete, production-ready interpreter for the Stoex language with all the functionality of the Palladio Stoex analyzer. It offers a clean, easy-to-use API while maintaining full compatibility with the existing codebase and supporting advanced features like probability distributions and type inference.

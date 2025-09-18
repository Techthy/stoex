# Stochastic Expressions (StoEx)


## File Structure
- `tools.vitruv.stoex`: Main StoEx language package (grammar and interpreter)
   - `src/main/java/tools/vitruv/stoex/Stoex.xText`: Xtext grammar for StoEx language
    - `src/main/java/tools/vitruv/stoex/interpreter`: Interpreter package for evaluating StoEx expressions (main entry point: `StoexEvaluator.java`)
    - `src/main/java/tools/vitruv/stoex/interpreter/visitors`: EvaluationVisitor and MeanVisitor main logic is the evaluation of StoEx expressions
    - `src/main/java/tools/vitruv/stoex/interpreter/operations`: Operations for evaluating StoEx expressions (e.g., addition, multiplication, distribution operations)



## Technology
The StoEx language is implemented using the Xtext framework. The implementation can be found in the [stoex](../stoex) project.

## Usage
To use the StoEx language, you need to integrate it into your project. This typically involves adding the necessary dependencies and configuring the Xtext environment. Detailed instructions can be found in the [stoex](../stoex) project documentation.




## Examples

Examples of StoEx expressions include:
- Basic arithmetic: `2 + 3 * 4`, `(x + y) / 2`
- Boolean logic: `true AND false`, `x > threshold`
- Conditional expressions: `active ? x : y`
- Mathematical functions: `sin(angle)`, `sqrt(value)`, `max(x, y
- Constants: `PI`, `E`
- Variables: `x`, `y`, `z`
- Probability distributions: `Normal(mu, sigma)`, `Lognormal(mu, sigma)`
- Distribution arithmetic: `Normal(0,1) + Normal(1,2)`, `Lognormal(0,1) * 2`


## StoEx API
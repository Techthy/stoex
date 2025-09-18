# Stochastic Expressions (StoEx)



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
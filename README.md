# hyperscan-java
[![Build Status](https://travis-ci.org/cerebuild/hyperscan-java.svg?branch=master)](https://travis-ci.org/cerebuild/hyperscan-java) [![](https://jitpack.io/v/cerebuild/hyperscan-java.svg)](https://jitpack.io/#cerebuild/hyperscan-java)

[hyperscan](https://github.com/intel/hyperscan) is a high-performance multiple regex matching library.

It uses hybrid automata techniques to allow simultaneous matching of large numbers (up to tens of thousands) of regular expressions and for the matching of regular expressions across streams of data.


This project is a third-party developed JNA based java wrapper for the [hyperscan](https://github.com/intel/hyperscan) project to enable developers to integrate hyperscan in their java (JVM) based projects.

## Limitations of hyperscan

hyperscan only supports a subset of regular expressions. Notable exceptions are for example backreferences and capture groups. Please read the [hyperscan developer reference](https://intel.github.io/hyperscan/dev-reference/) so you get a good unterstanding how hyperscan works and what the limitations are.

hyperscan will only run on x86 processors in 64-bit and 32-bit modes and takes advantage of special instruction sets, when available. Check the original [project documentation](https://intel.github.io/hyperscan/dev-reference/getting_started.html#hardware) to learn more.

## Dependencies
This wrapper only relies on the hyperscan shared library for it's functionality. It already contains precompiled 64-bit shared libraries for macOS and both 32-bit and 64-bit libraries for most glibc based Linux distributions.

#### Compile yourself
In case the precompiled binaries don't suite your usecase, you got to 
make sure you've got hyperscan compiled as a shared library on your system. On Linux and macOS a ```mkdir build && cd build && cmake -DBUILD_SHARED_LIBS=YES .. && make``` inside the git repositiory was enough. For more information about how to compile hyperscan visit the [project documentation](https://intel.github.io/hyperscan/dev-reference/).

Make sure you specify the system property ```jna.library.path``` using code or the command line to point to a location which includes the hyperscan shared libraries.

## Add it to your project
Visit https://jitpack.io/#cerebuild/hyperscan-java to add it to your project. Select the desired version and click on *Get it*. Then choose your build tool and follow the instructions. Gradle, maven, sbt and leiningen are supported.

Thanks to jitpack.io for hosting this project. 

For the JAR file only head to the [releases](https://github.com/cerebuild/hyperscan-java/releases) page.

## Simple example
```java
import com.gliwka.hyperscan.wrapper;

...

//we define a list containing all of our expressions
LinkedList<Expression> expressions = new LinkedList<Expression>();

//the first argument in the constructor is the regular pattern, the latter one is a expression flag
//make sure you read the original hyperscan documentation to learn more about flags
//or browse the ExpressionFlag.java in this repo.
expressions.add(new Expression("[0-9]{5}", EnumSet.of(ExpressionFlag.SOM_LEFTMOST)));
expressions.add(new Expression("Test", EnumSet.of(ExpressionFlag.CASELESS)));


//we precompile the expression into a database.
//you can compile single expression instances or lists of expressions
try {
    Database db = Database.compile(expressions);

    //initialize scanner - one scanner per thread!
    Scanner scanner = new Scanner();

    //allocate scratch space matching the passed database
    scanner.allocScratch(db);

    
    //provide the database and the input string
    //returns a list with matches
    //synchronized method, only one execution at a time (use more scanner instances for multithreading)
    List<Match> matches = scanner.scan(db, "12345 test string");

    //matches always contain the expression causing the match and the end position of the match
    //the start position and the matches string it self is only part of a matach if the
    //SOM_LEFTMOST is set (for more details refer to the original hyperscan documentation)
}
catch (CompileErrorException ce) {
    //gets thrown during  compile in case something with the expression is wrong
    //you can retrieve the expression causing the exception like this:
    Expression failedExpression = ce.getFailedExpression();
}
catch(Throwable e) {
    //edge cases like OOM, illegal platform etc.
}
```

## Javadoc

The javadoc is located [here](https://cerebuild.github.io/hyperscan-java/).



## Included packages
```com.gliwka.hyperscan.wrapper``` provides a java-style wrapper arround hyperscan.
```com.gliwka.hyperscan.jna``` implements the JNA interface and primitives used to call the hyperscan c library.

If you just want to use hyperscan in java, you only need to import ```com.gliwka.hyperscan.wrapper```.


## Currently not implemented
 * Serialization and Deserialization of databases
 * Extended expression syntax using [hs_compile_ext_multi()](http://intel.github.io/hyperscan/dev-reference/api_files.html#project0hs__compile_8h_1aacc508bea3042f1faba32c3818bfc2a3)

 Feel free to submit a pull request.

 ## Benchmark

 Use **sbt** to do benchmark and show elapsed time:

 ```
 sbt "testOnly com.gliwka.hyperscan.Benchmark -- -oD"
 ```

 ## License
 [BSD 3-Clause License](LICENSE)

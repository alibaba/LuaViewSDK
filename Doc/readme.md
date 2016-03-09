
# LuaView SDK 文档

## Introduction

LDoc is a software documentation tool which automatically generates API documentation
out of source code comments (doc comments). It is mainly targeted at Lua and documenting
Lua APIs, but it can also parse C with according doc comments for documenting Lua modules
implemented in C.

It is mostly compatible with [LuaDoc](http://keplerproject.github.com/luadoc/),
except that certain workarounds are no longer needed.
For instance, it is not so married to the idea that Lua modules should be defined using the
`module` function; this is not only a matter of taste since this has been deprecated in Lua
5.2.


Although there are a fair number of command-line options, the preferred route is to write a
`config.ld` configuration file in Lua format. By convention, if LDoc is simply invoked as
`ldoc .` it will read this file first. In this way, the aim is to make it very easy for
end-users to build your documentation using this simple command.

## Commenting Conventions

LDoc follows the conventions established by Javadoc and later by LuaDoc to document the
modules, functions, tables and types ("classes") of your API.

### Doc comments

Only 'doc comments' are parsed; these can be started with at least 3 hyphens, or by a empty
comment line with at least 3 hypens:

    --- summary.
    -- Description; this can extend over
    -- several lines

    -----------------
    -- This will also do.

Any module or script must start with a doc comment; any other files are ignored and a
warning issued. The only exception is if the module starts with an explicit `module`
statement.

If your coding standards require a boilerplate copyright notice, then the `-B` flag or
`boilerplate=true` will make LDoc ignore the first comment of each module.

Common commenting patterns like '---- (text) -----' are exempted, since they are often used
for programmer-facing documentation.


### Tags

All doc comments start with a summary sentence, that ends with a period or a question mark.
An optional description may follow. Normally the summary sentence will appear in the module
contents.

After this descriptive text, there will typically be _tags_ which are introduced with an @.
These follow the convention established by Javadoc and widely used in tools for other languages.

    --- Some doc comment
    -- @tag1 parameters for first tag
    -- @tag2 parameters for the second tag

The order of tags is not important, but as always, consistency is useful.

Here are all the tags known to LDoc:

  * **@module**  A Lua module containing functions and tables, which may be inside sections
  * **@classmod** Like **@module** but describing a class
  * **@submodule** A file containing definitions that you wish to put into the named _master_ module
  * **@script**  A Lua program
  * **@author** (multiple), **copyright**, **@license**, **@release** only used for _project-level_ tags like **@module**
  * **@function**, **@lfunction**. Functions inside a module
  * **@param**  formal arguments of a function (multiple)
  * **@return** returned values of a function (multiple)
  * **@raise** unhandled error thrown by this function
  * **@local** explicitly marks a function as not being exported (unless `--all`)
  * **@see** reference other documented items
  * **@usage** give an example of a function's use. (Has a somewhat different meaning when used
   with **@module**)
  * **@table** a Lua table
  * **@field** a named member of a table
  * **@section** starting a named section for grouping functions or tables together
  * **@type** a section which describes a class
  * **@within** puts the function or table into an implicit section
  * **@fixme**, **@todo** and **@warning** are _annotations_, which are doc comments that
  occur inside a function body.

The first important tag to know is the module tag:

However, you must either use the `--colon` flag or set `colon=true` in your `config.ld`.

In this style, types may be used directly if prefixed with '!' or '?' (for type-or-nil)

(see @{View}, rendered [here](http://stevedonovan.github.io/ldoc/examples/colon))

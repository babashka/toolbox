# The Clojure Toolbox

This is the source code for <https://www.clojure-toolbox.com>.

## Building

Project listing is stored in `projects.yml` in YAML format.

Build the new index page with Ruby: `script/generate.rb`.

Build the new index page with [babashka](https://github.com/borkdude/babashka/): `script/generate.clj`.

Deploy with `./upload.sh` (if you have permissions to the S3 bucket).

## License

Copyright © 2015 James Reeves

Released under the MIT license.

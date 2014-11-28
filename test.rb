#!/usr/bin/ruby

require 'FileUtils'

OUT_DIR = "/tmp/MongoALtest"
GRAMMAR_FILE = "./grammar/MongoAL.g4"

`export CLASSPATH=".:/usr/local/lib/antlr-4.4-complete.jar:$CLASSPATH"`
`alias antlr4=''`
`alias grun='java org.antlr.v4.runtime.misc.TestRig'`


if ARGV[0]
  testfiles = [ARGV[0]]
else
  testfiles = Dir["./tests/*"];
end

begin
  FileUtils.mkdir(OUT_DIR)
rescue Errno::EEXIST => e
  puts "Directory #{OUT_DIR} already exists. Continuing..."
end

#puts "Compiling Grammar file #{GRAMMAR_FILE}"
#puts `echo "#{OUT_DIR} y #{GRAMMAR_FILE}"`
#puts `java -jar /usr/local/lib/antlr-4.4-complete.jar -o #{OUT_DIR} #{GRAMMAR_FILE}`

for file in testfiles do
  puts "*** Analysing #{file}..."
  fileText = File.open(file).read
  fileText.split("/*").each do |testBlock|
    testBlock = testBlock.split("*/");
    comment = testBlock[0].to_s.strip;
    if comment.length > 0
      puts "--- New test: #{comment}"
      puts testBlock[1].to_s.strip
    end
  end
end
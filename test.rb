#!/usr/bin/ruby

require 'FileUtils'

OUT_DIR = "/tmp/MongoALtest"

GRAMMAR_NAME = "MongoAL"
GRAMMAR_FILE = "./grammar/MongoAL.g4"


START_RULE = "query"
TESTS_DIR ="./tests"

COLOR_FILE="\033[38;5;1m"
COLOR_TITLE="\033[38;5;3m"
COLOR_TESTBLOCK="\033[38;5;7m"
COLOR_TESTRESULT="\033[38;5;4m"

ENV["CLASSPATH"] = ".:#{OUT_DIR}:/usr/local/lib/antlr-4.4-complete.jar:$CLASSPATH"


if ARGV[0]
  testfiles = [ARGV[0]]
else
  testfiles = Dir["#{TESTS_DIR}/*"];
end

begin
  FileUtils.mkdir(OUT_DIR)
rescue Errno::EEXIST => e
  puts "Directory #{OUT_DIR} already exists. Continuing..."
end

puts "Compiling Grammar file #{GRAMMAR_FILE}"
puts `java -jar /usr/local/lib/antlr-4.4-complete.jar -o #{OUT_DIR} #{GRAMMAR_FILE}`
puts `javac -sourcepath #{OUT_DIR}/grammar -d #{OUT_DIR} #{OUT_DIR}/grammar/#{GRAMMAR_NAME}*.java`

for file in testfiles do
  puts "#{COLOR_FILE}*** Analysing #{file}..."
  fileText = File.open(file).read
  fileText.split("/*").each do |testBlock|
    testBlock = testBlock.split("*/");
    comment = testBlock[0].to_s.strip;
    if comment.length > 0
      testString = testBlock[1].to_s.strip
      puts "#{COLOR_TITLE}--- New test: #{comment}"
      puts "#{COLOR_TESTBLOCK}#{testString}"
      print COLOR_TESTRESULT
      puts `echo "#{testString}" | java org.antlr.v4.runtime.misc.TestRig #{GRAMMAR_NAME} #{START_RULE} -gui`
    end
  end
end

# reset console colors
`reset`
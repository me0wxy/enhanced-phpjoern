package tools.php.ast2cpg;

import org.apache.commons.cli.ParseException;

import tools.CommonCommandLineInterface;

public class CommandLineInterface extends CommonCommandLineInterface
{
	String nodeFile;
	String edgeFile;
	static String mode;

	public String getNodeFile()
	{
		return nodeFile;
	}

	public String getEdgeFile()
	{
		return edgeFile;
	}

	public void printHelp()
	{
		formatter.printHelp("importer <nodes.csv> <edges.csv> [relax | strict(default)]...", options);
	}

	public void parseCommandLine(String[] args) throws ParseException
	{
		if (args.length < 2 || args.length > 3)
			throw new RuntimeException("Please supply a node and an edge file");

		cmd = parser.parse(options, args);

		String[] arguments = cmd.getArgs();
		nodeFile = arguments[0];
		edgeFile = arguments[1];

		if (args.length == 2)
			mode = "strict";
		else{
			mode = arguments[2];
			if (!"relax".equals(mode) && !"strict".equals(mode))
			{
				throw new ParseException("Please choose one mode: strict(by default) or relax");
			}
		}

	}

	public static String getParseMode()
	{
		return mode.replace("-", "");
	}

}

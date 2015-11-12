<a href="https://www.portfolioeffect.com/">
  <img width="200" src="https://www.portfolioeffect.com/img/logo/portfolioeffect-logo-full-200-950.png" alt="PortfolioEffect">
</a>

# PortfolioEffectHFT - Java Library 

Java API to PortfolioEffect Quant service for high frequency trading (HFT) strategy backtests, intraday portfolio 
analysis and portfolio optimization.

## About PortfolioEffect

PortfolioEffect platform employes high frequency microstructure model pipeline, cloud computing and server-side 
market data to enable classic portfolio analysis at intraday horizons.

## Building from Source

Stable builds are in the You need to have Maven installed to build the jar from source. 
After that in the folder with pom.xml run:

	mvn package

## Account Credentials

### Locate API Credentials

All portfolio computations are performed on PortfolioEffect cloud servers.
To obtain a free non-professional account, you need to follow a quick sign-up
process on our [website][]

Please use a valid sign-up address - it will be used to email your
account activation link to enable API access then log in to you account and 
locate your API credentials on the main page:

<img src="https://www.portfolioeffect.com/img/screenshots/quant/tutorials/api-settings.png" alt="API Credentials">

### Set API Credentials

Run the following commands to set your account API credentials for the
PortfolioEffectHFT Java Library.

	util_setCredentials("API Username", "API Password", "API Key")

Credentials are not stored between sessions. You would need to repeat
this procedure every time you start a new JVM session or if you changed your account 
password.

You are now ready to call PortfolioEffectHFT methods.

## Portfolio Construction

### User Data

Users may supply their own historical datasets for index and position entries. 
This external data could be one a OHLC bar column element (e.g. 1-second close prices) or a vector of actual transaction prices that contains non-equidistant data points. 
You might want to pre-pend at least N =(4 x windowLength) data points to the
beginning of the interval of interest which would be used for initial calibration of portfolio metrics.

#### Create Portfolio 

Method [portfolio_create()][] takes a vector of index prices in the format (UTC timestamp, price) with UTC
timestamp expressed in milliseconds from 1970-01-01 00:00:00 EST.
		
	Time          Value
	[1,] 1412256601000 99.30
	[2,] 1412256602000 99.33
	[3,] 1412256603000 99.30
	[4,] 1412256604000 99.26
	[5,] 1412256605000 99.36
	[6,] 1412256606000 99.36
	[7,] 1412256607000 99.36
	[8,] 1412256608000 99.38
	[9,] 1412256609000 99.40
	[10,] 1412256610000 99.37
		  
If index symbol is specified, it is silently ignored.

	// Create portfolio
	Portfolio portfolio = portfolio_create(indexPrice, indexTime);


#### Add Positions

Positions are added using [portfolio_addPosition()][] 
with 'priceData' in the same format as index price.

	// Single position without rebalancing
	portfolio_addPosition(portfolio, 
				"AAPL", 
				100, 
				aaplPrice, 
				aaplTime);


	// Single position with rebalancing
	int posQuantity = new int[] {300,150};
	String posTime = new String[] {"2014-09-01 09:00:00","2014-09-07 14:30:00"};
	portfolio_addPosition(portfolio, 
				"AAPL", 
				posQuantity,
				posTime, 
				aaplPrice, 
				aaplTime);


### Server Data

At PortfolioEffect we are capturing and storing 1-second intraday bar history for a 
all NASDAQ traded equites (see [symbology][]).

This server-side dataset spans from January 2013 to the latest trading time minus five minutes. 
It could be used to construct asset portfolios and compute intraday portfolio metrics.

#### Create Portfolio

Method [portfolio_create()][] creates new asset portfolio or overwrites an existing portfolio object with the
same name.

When using server-side data, it only requires a time interval that would be
treated as a default position holding period unless positions are added with rebalancing.
Index symbol could be specified as well with a default value of "SPY" - SPDR S&P 500 ETF Trust.

Interval boundaries are passed in the following format:
		  
* "yyyy-MM-dd HH:MM:SS" (e.g. "2014-10-01 09:30:00")
* "yyyy-MM-dd" (e.g. "2014-10-01")
* "t-N" (e.g. "t-5" is latest trading time minus 5 days)
* UTC timestamp in milliseconds (mills from "1970-01-01 00:00:00") in EST time zone

For example:
  
	// Timestamp in "yyyy-MM-dd HH:MM:SS" format
	Portfolio portfolio = portfolio_create("2014-10-01 09:30:00",
								 	       "2014-10-02 16:00:00");
			
	// Timestamp in "yyyy-MM-dd" format
	portfolio=portfolio_create("2014-10-01", 
							   "2014-10-02");
			
	// Timestamp in "t-N" format
	portfolio=portfolio_create("t-5", 
							   "t");

#### Add Positions
Positions are added by calling [portfolio_addPosition()][]
method on a portfolio object with a list of symbols and quantities. For
positions that were rebalanced or had non-default holding periods a 'time' argument could be used to specify rebalancing timestamps.


	// Single position without rebalancing
	portfolio_addPosition(portfolio,
					      "GOOG", 
					       100);
			
	// Multiple positions without rebalancing
	String symbols = new String[] {"C","GOOG"};
	int initQuantity = new int[] {500,600};
	portfolio_addPosition(portfolio,
						  symbols
						  initQuantity);
			
	// Single position with rebalancing
	int posQuantity = new int[] {300,150};
	String posTime = new String[] {"2014-09-01 09:00:00","2014-09-07 14:30:00"};
	portfolio_addPosition(portfolio,
						  "AAPL", 
						  posQuantity, 
						  posTime);


## License

This library is released under the GPLv3 license. See the file LICENSE.

Usage of this package with PortfolioEffect services shall be subject to the [Terms of Service][PortfolioEffect Terms].

## Copyright

Copyright &copy; 2015 PortfolioEffect

[PortfolioEffect Terms]: https://www.portfolioeffect.com/docs/terms
[website]: https://www.portfolioeffect.com/registration
[portfolio_create()]: https://www.portfolioeffect.com/docs/platform/quant/functions/general-functions/portfolio-create
[portfolio_addPosition()]: https://www.portfolioeffect.com/docs/platform/quant/functions/general-functions/portfolio-add-position
[symbology]: https://www.portfolioeffect.com/docs/symbology
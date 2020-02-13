package io.humourmind.rsocketserver.util;

import static java.util.Arrays.stream;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import io.humourmind.rsocketserver.domain.Quote;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class QuoteGenerator {

	enum EntQuote {

		// @formatter:off
		PVTL("PVTL", 11.59),
		VMW("VMW", 169.60),
		DELL("DELL", 54.99),
		FB("FB", 189.50),
		AAPL("AAPL", 199.80),
		AMZN("AMZN", 1_904.28),
		NFLX("NFLX", 370.02),
		GOOGL("GOOGL", 1_064.54),
		MSFT("MSFT", 131.58);
		// @formatter:on

		private String ticker;
		private double price;

		EntQuote(String ticker, double price) {
			this.ticker = ticker;
			this.price = price;
		}

		public String getTicker() {
			return ticker;
		}

		public double getPrice() {
			return price;
		}
	}

	private final MathContext mathContext = new MathContext(2);
	private final Random random = new Random();
	private final List<Quote> prices = new ArrayList<>();
	private final Flux<Quote> quoteStream;
	private final AtomicInteger counter = new AtomicInteger(0);

	/**
	 * Bootstraps the generator with tickers and initial prices
	 */
	QuoteGenerator() {
		stream(EntQuote.values()).forEach(e -> this.prices.add(
				new Quote(e.getTicker(), new BigDecimal(e.getPrice(), this.mathContext),
						Instant.now(), counter.get())));
		this.quoteStream = Flux.interval(Duration.ofSeconds(1)).onBackpressureDrop()
				.flatMap(e -> Flux.fromIterable(prices.stream().map(baseQuote -> {
					BigDecimal priceChange = baseQuote.getPrice().multiply(
							new BigDecimal(0.05 * this.random.nextDouble()),
							this.mathContext);
					return new Quote(baseQuote.getTicker(),
							baseQuote.getPrice().add(priceChange), Instant.now(),
							counter.incrementAndGet());
				}).collect(Collectors.toList()))).share().cache().retry();
	}

	public Flux<Quote> getQuoteStream() {
		return quoteStream;
	}

	public Mono<Quote> getQuote(String symbol) {
		return getQuoteStream(symbol).take(1).single();
	}

	public Flux<Quote> getQuoteStream(String symbol) {
		return quoteStream.filter(quote -> quote.getTicker().equalsIgnoreCase(symbol))
				.timeout(Duration.ofSeconds(3), Mono.just(new Quote()));
	}

	public Flux<Quote> getFilteredQuoteStream(Flux<String> symbol) {
		return symbol.flatMap(this::getQuoteStream).switchIfEmpty(Mono.just(new Quote()));
	}

}

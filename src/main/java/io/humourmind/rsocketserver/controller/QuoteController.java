package io.humourmind.rsocketserver.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import io.humourmind.rsocketserver.domain.Quote;
import io.humourmind.rsocketserver.util.QuoteGenerator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class QuoteController {

	private final QuoteGenerator quoteGenerator;

	public QuoteController(QuoteGenerator quoteGenerator) {
		this.quoteGenerator = quoteGenerator;
	}

	@MessageMapping("all-quote-stream")
	public Flux<Quote> getAllQuotesStream() {
		return quoteGenerator.getQuoteStream();
	}

	@MessageMapping("filtered-quote-stream")
	public Flux<Quote> getFilteredQuotesStream(Flux<String> symbol) {
		return quoteGenerator.getFilteredQuoteStream(symbol);
	}

	@MessageMapping("a-quote-stream")
	public Flux<Quote> getAQuoteStream(String symbol) {
		return quoteGenerator.getQuoteStream(symbol);
	}

	@MessageMapping("a-quote")
	public Mono<Quote> getAQuote(String symbol) {
		return quoteGenerator.getQuote(symbol);
	}

}

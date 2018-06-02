INSERT INTO PUBLIC.MODELS (ID, NAME, ADAPTER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, OPEN, DESCRIPTION) VALUES (1, 'Original Datumbox Twitter Sentiment Analysis', 0, 'es.uned.adapters.sentiment.Datumbox', 'en', 'TwitterSentimentAnalysis', false, 1, TRUE, 'Desc');
INSERT INTO PUBLIC.MODELS (ID, NAME, ADAPTER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, OPEN, DESCRIPTION) VALUES (2, 'Original Datumbox Sentiment Analysis', 0, 'es.uned.adapters.sentiment.Datumbox', 'en', 'SentimentAnalysis', false, 1, TRUE , 'Desc');
INSERT INTO PUBLIC.MODELS (ID, NAME, ADAPTER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, OPEN, DESCRIPTION) VALUES (3, 'Original Lingpipe Polarity Movie Reviews', 0, 'es.uned.adapters.sentiment.LingPipe', 'en', 'OriginalPolarityMovieReviews', false, 1, TRUE , 'Desc');
INSERT INTO PUBLIC.MODELS (ID, NAME, ADAPTER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, OPEN, DESCRIPTION) VALUES (4, 'Original Datumbox Subjectivity Analysis', 1, 'es.uned.adapters.subjectivity.Datumbox', 'en', 'SubjectivityAnalysis', false, 2, TRUE, 'Desc');
INSERT INTO PUBLIC.MODELS (ID, NAME, ADAPTER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, OPEN, DESCRIPTION) VALUES (5, 'Original Lingpipe Subjectivity Analysis', 1, 'es.uned.adapters.subjectivity.Lingpipe', 'en', 'OriginalSubjectivity', false, 2, TRUE , 'Desc');

INSERT INTO PUBLIC.LANGUAGE_MODELS (ID, NAME, CLASSIFIER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, PUBLIC, DESCRIPTION) VALUES (1, 'Original Datumbox Twitter Sentiment Analysis', 0, 'es.uned.adapters.sentiment.Datumbox', 'en', 'TwitterSentimentAnalysis', false, 1, TRUE, 'Desc');
INSERT INTO PUBLIC.LANGUAGE_MODELS (ID, NAME, CLASSIFIER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, PUBLIC, DESCRIPTION) VALUES (2, 'Original Datumbox Sentiment Analysis', 0, 'es.uned.adapters.sentiment.Datumbox', 'en', 'SentimentAnalysis', false, 1, TRUE , 'Desc');
INSERT INTO PUBLIC.LANGUAGE_MODELS (ID, NAME, CLASSIFIER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, PUBLIC, DESCRIPTION) VALUES (3, 'Original Lingpipe Polarity Movie Reviews', 0, 'es.uned.adapters.sentiment.LingPipe', 'en', 'OriginalPolarityMovieReviews', false, 1, TRUE , 'Desc');
INSERT INTO PUBLIC.LANGUAGE_MODELS (ID, NAME, CLASSIFIER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, PUBLIC, DESCRIPTION) VALUES (4, 'Original Datumbox Subjectivity Analysis', 1, 'es.uned.adapters.subjectivity.Datumbox', 'en', 'SubjectivityAnalysis', false, 2, TRUE, 'Desc');
INSERT INTO PUBLIC.LANGUAGE_MODELS (ID, NAME, CLASSIFIER_TYPE, ADAPTER_CLASS, LANGUAGE, LOCATION, TRAINABLE, OWNER, PUBLIC, DESCRIPTION) VALUES (5, 'Original Lingpipe Subjectivity Analysis', 1, 'es.uned.adapters.subjectivity.Lingpipe', 'en', 'OriginalSubjectivity', false, 2, TRUE , 'Desc');
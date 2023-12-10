package ru.clevertec.repository;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.bson.Document;
import ru.clevertec.entity.Account;
import ru.clevertec.util.Constants;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AccountRepository implements Repository<Account> {

    private static final Repository<Account> INSTANCE = new AccountRepository();
    private static final String MONGO_DB_URL = "mongodb://localhost:27017";
    private static final String MONGO_DB_NAME = "temp-db";
    private static final String COLLECTION_NAME = "accounts";

    public static Repository<Account> getInstance() {
        return INSTANCE;
    }

    @Override
    public List<Account> findByParams(String signAppId, String type, String currency, String status, String paymentKindCode) {
        List<Account> accounts = new ArrayList<>();
        try (MongoClient mongoClient = MongoClients.create(MONGO_DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);
            boolean collectionExists = mongoClient.getDatabase(MONGO_DB_NAME).listCollectionNames()
                    .into(new ArrayList<>()).contains(COLLECTION_NAME);
            if (!collectionExists) {
                database.createCollection(COLLECTION_NAME);
            }
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            Document document = buildDocumentByParams(signAppId, type, currency, status, paymentKindCode);
            FindIterable<Document> cursor = collection.find(document);
            log.info("signAppId {}, type {}, currency {}, status {}, paymentKindCode {}", signAppId, type, currency, status, paymentKindCode);
            try (MongoCursor<Document> cursorIterator = cursor.cursor()) {
                while (cursorIterator.hasNext()) {
                    Account account = buildAccount(cursorIterator.next());
                    log.info("Account: " + account);
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    @Override
    public Account save(Account account) {
        try (MongoClient mongoClient = MongoClients.create(MONGO_DB_URL)) {
            MongoDatabase database = mongoClient.getDatabase(MONGO_DB_NAME);
            boolean collectionExists = mongoClient.getDatabase(MONGO_DB_NAME).listCollectionNames()
                    .into(new ArrayList<>()).contains(COLLECTION_NAME);
            if (!collectionExists) {
                database.createCollection(COLLECTION_NAME);
            }
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
            Document document = buildDocument(account);
            InsertOneResult result = collection.insertOne(document);
            BsonValue insertedId = result.getInsertedId();
            String id = insertedId.asObjectId().getValue().toString();
            account.setId(id);
            return account;
        }
    }

    private static Document buildDocument(Account account) {
        return new Document()
                .append(Constants.SIGN_APP_ID, account.getSignAppId())
                .append(Constants.TYPE, account.getType())
                .append(Constants.CURRENCY, account.getCurrency())
                .append(Constants.STATUS, account.getStatus())
                .append(Constants.PAYMENT_KIND_CODE, account.getPaymentKindCode());
    }

    private static Document buildDocumentByParams(String signAppId, String type, String currency, String status, String paymentKindCode) {
        Document document = new Document();
        if (signAppId != null) {
            document.put(Constants.SIGN_APP_ID, signAppId);
        }
        if (type != null) {
            document.put(Constants.TYPE, type);
        }
        if (currency != null) {
            document.put(Constants.CURRENCY, currency);
        }
        if (status != null) {
            document.put(Constants.STATUS, status);
        }
        if (paymentKindCode != null) {
            document.put(Constants.PAYMENT_KIND_CODE, paymentKindCode);
        }
        return document;
    }

    private static Account buildAccount(Document document) {
        return Account.builder()
                .id(document.getObjectId(Constants.ID).toString())
                .signAppId(document.getString(Constants.SIGN_APP_ID))
                .type(document.getString(Constants.TYPE))
                .currency(document.getString(Constants.CURRENCY))
                .status(document.getString(Constants.STATUS))
                .paymentKindCode(document.getString(Constants.PAYMENT_KIND_CODE))
                .build();
    }
}

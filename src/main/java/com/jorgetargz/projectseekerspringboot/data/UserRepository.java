package com.jorgetargz.projectseekerspringboot.data;

import com.jorgetargz.projectseekerspringboot.data.collections.user.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByFirebaseId(String firebaseUID);
}


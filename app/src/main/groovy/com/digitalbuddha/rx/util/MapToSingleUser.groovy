package com.digitalbuddha.rx.util
import com.digitalbuddha.rx.model.User
import rx.functions.Func1

import static com.digitalbuddha.rx.ui.GithubActivity.getRandomIndex

/**
 * Created by Nakhimovich on 2/19/15.
 */
public class MapToSingleUser implements Func1<ArrayList<User>, User> {
    @Override
    public User call(ArrayList<User> users) {
        return users.get(getRandomIndex(users.size()));
    }


}

package io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.callback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testBundles.successfulTestBundles.UpdatableSucceedingTestEntityBundle;
import io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntityModification;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class UpdateTestEntityBundle<E extends IdentifiableEntity> {
    private E modifiedEntity;
    private PostUpdateCallback<E> postUpdateCallback = (entity) -> {};

    /**
     * Set this, if you dont want the default {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.TestRequestEntity} that will be generated by the {@link io.github.vincemann.generic.crud.lib.test.controller.springAdapter.testRequestEntity.factory.TestRequestEntityFactory}
     * but instead, you want to edit it, you can do it via {@link TestRequestEntityModification} for this specific tested Entity.
     *
     * This will only be used for update Tests. For other Tests see {@link UpdatableSucceedingTestEntityBundle}.
     */
    @Nullable
    private TestRequestEntityModification testRequestEntityModification;

    public UpdateTestEntityBundle(E modifiedEntity) {
        this.modifiedEntity = modifiedEntity;
    }

    @Builder
    public UpdateTestEntityBundle(E modifiedEntity, PostUpdateCallback<E> postUpdateCallback, @Nullable TestRequestEntityModification testRequestEntityModification) {
        this.modifiedEntity = modifiedEntity;
        this.postUpdateCallback = postUpdateCallback;
        this.testRequestEntityModification = testRequestEntityModification;
        if(this.postUpdateCallback==null){
            this.postUpdateCallback= (entity) -> {};
        }
    }


}
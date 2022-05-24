<script setup>
import { onBeforeMount, ref } from "vue";
import { getCategories } from "../service/api";

const categories = ref([]);
const currentCategory = ref({});

onBeforeMount(async () => {
  const c = await getCategories();
  // sortDescendingByDateInPlace(c, (c) => c.eventStartTime);
  categories.value = c;
});

function selectCategory(category) {
  if (isEditing.value) {
    return;
  }
  currentCategory.value = category;
}

const isEditing = ref(false);
</script>

<template>
  <div class="py-8 px-12 max-w-[1440px] flex mx-auto">
    <div class="flex flex-col">
      <div class="mb-4 font-semibold">
        All Categories: {{ categories.length }} Categories
      </div>
      <div>
        <tr
          v-if="categories.length > 0"
          v-for="category in categories"
          @click="selectCategory(category)"
          class="my-10 bg-white rounded-lg border-b border-gray-200 shadow-black/5 relative cursor-pointer hover:bg-gray-50 transition box-border"
          :class="[
            {
              'z-10 bg-blue-200/10 hover:bg-blue-200/20 ring-2 ring-blue-400/50 ':
                currentCategory.id === category.id
            }
          ]"
        >
          <td class="py-2 px-2">
            <span class="font-medium text-gray-800">{{
              category.eventCategoryName
            }}</span>
          </td>
        </tr>
        <div class="p-4 bg-gray-100 relative w-4/12" v-if="currentCategory.id">
          <CategoryDetails class="sticky top-24" :category="currentCategory" />
        </div>
      </div>
    </div>
  </div>
</template>

<style></style>

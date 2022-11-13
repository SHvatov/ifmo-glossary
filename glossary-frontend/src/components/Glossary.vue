<template>
  <b-container fluid id="glossary-container">
    <b-row>
      <b-col lg="8">
        <b>
          <b-form-group
            label="Сортировать по наименованию"
            label-for="sort-by-name"
            label-cols-sm="6"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-form-select
              id="sort-by-name"
              v-model="sortOrder"
              size="sm"
              class="lg"
            >
              <option :value="'ASC'">По возрастанию</option>
              <option :value="'DESC'">По убыванию</option>
            </b-form-select>
          </b-form-group>
        </b>
      </b-col>
    </b-row>

    <b-row>
      <b-col lg="8">
        <b>
          <b-form-group
            label="Фильтровать по наименованию"
            label-for="filter-input"
            label-cols-sm="6"
            label-align-sm="right"
            label-size="sm"
            class="lg"
          >
            <b-input-group size="sm">
              <b-form-input
                id="filter-input"
                v-model="filter"
                type="text"
                placeholder="Введите подстроку для фильтрации..."
              >
              </b-form-input>

              <b-input-group-append>
                <b-button @click="clearFilter()"> Очистить </b-button>
              </b-input-group-append>
            </b-input-group>
          </b-form-group>
        </b>
      </b-col>
    </b-row>

    <b-row>
      <b-col lg="3">
        <b-button @click="loadGlossary()"> Применить фильтры </b-button>
      </b-col>
    </b-row>

    <b-table
      striped
      hover
      ref="glossary_table"
      :items="items"
      :fields="fields"
      responsive="sm"
      id="glossary-table"
    >
      <template #cell(link)="link">
        <a href="#" @click="redirectToSource(link.value)">Ссылка на источник</a>
      </template>
    </b-table>
  </b-container>
</template>

<script lang="ts">
import $ from "jquery";

export default {
  data() {
    return {
      sortOrder: "ASC",
      filter: "",
      fields: [
        {
          key: "name",
          label: "Термин",
          sortable: true,
        },
        { key: "description", label: "Описание" },
        {
          key: "link",
          label: "Источник",
        },
      ],
      items: [],
    };
  },
  mounted() {
    this.loadGlossary();
  },
  methods: {
    loadGlossary() {
      let getGlossaryItemsQuery = `{ 
        getGlossaryItems(sort: ${this.sortOrder}, nameFilter: "${this.filter}") { 
          name description link 
        } 
      }`;

      console.log(getGlossaryItemsQuery);

      fetch("http://localhost:8080/graphql", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Accept: "application/json",
        },
        body: JSON.stringify({ query: getGlossaryItemsQuery }),
      })
        .then((r) => r.json())
        .then((data) => {
          this.items = data.data.getGlossaryItems;
          this.$refs.glossary_table.refresh();
        });
    },
    clearFilter() {
      // hack due to using not production ready bootstrap for vue 3
      $("#filter-input").val("");
      this.filter = "";
    },
    redirectToSource(link: string) {
      console.log(link);
      window.location.href = link;
    },
  },
};
</script>